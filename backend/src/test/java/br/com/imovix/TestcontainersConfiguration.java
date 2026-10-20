package br.com.imovix;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * PostgreSQL real e descartável para testes de integração.
 *
 * <p>A imagem é a mesma do {@code compose.yaml} (propriedade {@code postgres.test.image} do POM),
 * evitando divergência entre ambiente local e testes.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  private static final String DEFAULT_IMAGE = "postgres:18-alpine";

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresContainer() {
    var image = System.getProperty("postgres.test.image", DEFAULT_IMAGE);
    return new PostgreSQLContainer(DockerImageName.parse(image));
  }
}
