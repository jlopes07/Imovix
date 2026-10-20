# =============================================================================
# Imovix Backend — Dockerfile Multi-stage seguro
# =============================================================================

# --- Estágio 1: Build --------------------------------------------------------
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /build

# Copia arquivos do wrapper e pom para cache eficiente de dependências
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B -ntp

# Copia código-fonte e compila
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B -ntp

# Extrai camadas para JVM container otimizada (Spring Boot Layer Tools)
RUN java -Djarmode=tools -jar target/*.jar extract --layers --destination extracted/

# --- Estágio 2: Runtime seguro ----------------------------------------------
FROM eclipse-temurin:25-jre-alpine AS runtime

# Cria usuário não-root com menor privilégio
RUN addgroup -S imovix && adduser -S imovix -G imovix

WORKDIR /app

# Copia as camadas geradas
COPY --from=builder --chown=imovix:imovix /build/extracted/dependencies/ ./
COPY --from=builder --chown=imovix:imovix /build/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=imovix:imovix /build/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=imovix:imovix /build/extracted/application/ ./

USER imovix:imovix

# Configurações de JVM seguras e otimizadas para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
