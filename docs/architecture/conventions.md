# Convenções de Código e Diretrizes Arquiteturais — Imovix

Este documento estabelece os padrões técnicos, regras arquiteturais e práticas de segurança obrigatórias para o desenvolvimento da plataforma **Imovix**.

---

## 1. Estilo Arquitetural: Monólito Modular

O backend adota o estilo **Monólito Modular** em Java LTS (25) com Spring Boot. Todos os módulos de negócio residem no mesmo artefato executável, mas mantêm **fronteiras de domínio estritas** validadas continuamente por testes arquiteturais automatizados (**ArchUnit**).

### Organização Interna de Cada Módulo

Cada módulo de negócio (ex.: `properties`, `contacts`, `crm`, `subscriptions`) deve seguir a separação em quatro camadas:

```
br.com.imovix.<modulo>/
├── api/             # Contratos HTTP REST, DTOs de entrada/saída, validações Bean Validation (@Valid)
├── application/     # Casos de uso (Services), orquestração de transações (@Transactional)
├── domain/          # Entidades de negócio, Value Objects, Enums e interfaces de Repository
└── infrastructure/  # Implementação de banco (Spring Data JPA), clientes HTTP externos, brokers
```

### Regras de Dependência (Validadas pelo ArchUnit)

1. **`domain` é puro**:
   - NÃO depende de `api`, `application` nem `infrastructure`.
   - NÃO depende de pacotes web (`org.springframework.web..`, `jakarta.servlet..`).
2. **`application` orquestra**:
   - Depende de `domain`.
   - NÃO depende de `api` nem de implementações concretas em `infrastructure`.
3. **`api` expõe**:
   - Depende de `application` e tipos de domínio quando necessário.
   - NÃO acessa `infrastructure` diretamente.
   - Controllers devem residir no pacote `api` e ter sufixo `Controller`.
4. **Injeção de Dependências**:
   - Proibida injeção por campo (`@Autowired` em atributos privados).
   - Use injeção por construtor (favorece imutabilidade e testes unitários diretos).
5. **Logs**:
   - Proibido uso de `System.out` ou `java.util.logging`. Utilize sempre SLF4J (`org.slf4j.Logger`).

---

## 2. Multi-tenancy e Isolamento de Dados

A Imovix atende múltiplas imobiliárias (tenants). O isolamento de dados é um requisito mandatório de segurança desde o primeiro CRUD:

1. **Origem do Tenant**:
   - O `tenant_id` da requisição é **sempre derivado da sessão ou token autenticado do usuário**.
   - **NUNCA confiar em `tenant_id` enviado no payload JSON ou query parameter pelo cliente**.
2. **Escopo Obrigatório**:
   - Toda consulta (`SELECT`), alteração (`UPDATE`) e exclusão (`DELETE`) deve aplicar o filtro da empresa ativa.
3. **Identificadores**:
   - Utilizar **UUID** para entidades expostas publicamente ou via API, mitigando enumeração de registros (IDOR).
4. **Testes Negativos**:
   - Toda funcionalidade deve incluir testes garantindo que o Tenant A receba `404 Not Found` ou `403 Forbidden` ao tentar acessar qualquer recurso do Tenant B.

---

## 3. Segurança e Privacidade (LGPD & OWASP)

1. **Fail-Closed por Padrão**:
   - Toda nova rota é fechada (`denyAll()`) até que seja explicitamente liberada na configuração de segurança com permissões declaradas.
2. **CORS Rígido**:
   - Nunca utilizar `allowedOrigins("*")`. Origens devem ser configuradas de forma explícita via variável de ambiente.
3. **Segredos e Credenciais**:
   - **NENHUM segredo no repositório**. Arquivos `.env` reais, certificados, senhas e chaves são ignorados pelo `.gitignore`.
   - Use `.env.example` para documentar variáveis obrigatórias.
4. **Trilha de Auditoria e Logs**:
   - Nunca registrar em logs senhas, tokens de acesso, dados completos de cartão ou dados pessoais sensíveis desnecessários.
   - Incluir `X-Correlation-Id` em todas as requisições para rastreabilidade.
5. **Valores Monetários e Datas**:
   - Dinheiro: sempre `BigDecimal` / `numeric` no banco. Nunca `float` ou `double`.
   - Datas: sempre em **UTC** (`Instant` no Java, `TIMESTAMP WITH TIME ZONE` no PostgreSQL).

---

## 4. Padrões de Código e Ferramental

| Camada | Padrão / Ferramenta | Comando de Validação |
| :--- | :--- | :--- |
| **Java Formatter** | Google Java Style via Spotless | `./mvnw spotless:check` / `./mvnw spotless:apply` |
| **Java Build & Linter** | Maven Compiler com `-Xlint:all` e Enforcer | `./mvnw clean verify` |
| **Testes Arquiteturais** | ArchUnit JUnit 5 | Executado no `mvnw test` |
| **Testes de Integração** | Testcontainers + PostgreSQL 18 | Executado no `mvnw verify` |
| **Frontend Formatter/Lint** | Oxlint / ESLint + TypeScript strict | `npm run build` |
| **Linhas & Encodings** | UTF-8 e LF em todo o projeto | Configurado no `.editorconfig` e `.gitattributes` |
