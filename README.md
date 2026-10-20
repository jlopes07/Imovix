# Imovix — SaaS de Gestão Imobiliária

[![Java Version](https://img.shields.io/badge/Java-25%20LTS-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue.svg)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-19-61dafb.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178c6.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-8-646cff.svg)](https://vite.dev/)
[![Code Style: Google](https://img.shields.io/badge/code%20style-Google-blue.svg)](https://google.github.io/styleguide/javaguide.html)

> Plataforma web multiempresa (multi-tenant) para gestão e operação de imobiliárias, incorporadoras e corretores autônomos. Projetada com foco em alta disponibilidade, conformidade com a LGPD e API REST versionada (`/api/v1`) pronta para futura expansão mobile.

---

## 1. Visão Geral da Arquitetura

O sistema é concebido como um **Monólito Modular**:
- **Backend:** Java 25 LTS com Spring Boot 4.1.1, Spring Data JPA, Spring Security e Flyway.
- **Frontend:** React 19 + TypeScript com Vite e Design System moderno em Vanilla CSS.
- **Banco de Dados:** PostgreSQL 18 em esquema multiempresa compartilhado com isolamento estrito por `tenant_id` e políticas de menor privilégio.
- **Isolamento de Domínio:** Fronteiras arquiteturais impostas e verificadas via **ArchUnit** (`api -> application -> domain <- infrastructure`).

```
                    NAVEGADOR / MOBILE FUTURO
                                │
                              HTTPS
                                ▼
                       FRONTEND WEB (React)
                                │
                          REST /api/v1
                                ▼
                      BACKEND (Spring Boot)
   ├── identity          (usuários, autenticação, perfis e convites)
   ├── tenancy           (empresas clientes, isolamento de tenant)
   ├── properties        (cadastro de imóveis, características e fotos)
   ├── contacts          (proprietários, interessados e dados LGPD)
   ├── crm               (funil de vendas, leads, visitas e tarefas)
   ├── listings          (vitrine pública otimizada para SEO)
   ├── subscriptions     (planos, faturamento e webhooks de pagamento)
   ├── notifications     (e-mails e fila de envio assíncrono outbox)
   ├── audit             (trilha de auditoria imutável de acessos e alterações)
   └── platformadmin     (painel do operador do SaaS e métricas globais)
                                │
                 ┌──────────────┴──────────────┐
                 ▼                             ▼
       PostgreSQL 18 (Local/RDS)       Object Storage (S3 / R2)
```

---

## 2. Estrutura do Repositório

```
Imovex/
├── backend/                       # Aplicação Java Spring Boot
│   ├── .mvn/                      # Configurações do Maven Wrapper e JVM
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/br/com/imovix/
│   │   │   │   ├── ImovixApplication.java
│   │   │   │   ├── config/        # Configurações transversais (Clock UTC, etc.)
│   │   │   │   ├── security/      # Configuração fail-closed e CORS rigoroso
│   │   │   │   ├── tenancy/       # Resolução e escopo do tenant autenticado
│   │   │   │   ├── shared/        # Utilitários compartilhados estritos
│   │   │   │   ├── identity/      # Gestão de contas e usuários
│   │   │   │   ├── companies/     # Organizações clientes (imobiliárias)
│   │   │   │   ├── properties/    # Imóveis, fotos e características
│   │   │   │   ├── contacts/      # Pessoas físicas, jurídicas e preferências
│   │   │   │   ├── crm/           # Leads, funil, atividades e visitas
│   │   │   │   ├── listings/      # Divulgação pública controlada
│   │   │   │   ├── subscriptions/ # Planos, limites e pagamentos
│   │   │   │   ├── notifications/ # Outbox de mensagens e notificações
│   │   │   │   ├── audit/         # Auditoria de ações sensíveis
│   │   │   │   └── platformadmin/ # Painel administrativo do SaaS
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-local.yml
│   │   └── test/java/br/com/imovix/
│   │       ├── ArchitectureTest.java         # Testes arquiteturais com ArchUnit
│   │       ├── ImovixApplicationIT.java      # Testes de integração com Testcontainers
│   │       └── security/                     # Testes de isolamento e CORS
│   ├── Dockerfile                 # Multi-stage build seguro (non-root)
│   ├── compose.yaml               # Docker Compose com PostgreSQL 18
│   ├── .env.example               # Template de variáveis de ambiente do backend
│   └── pom.xml                    # Configuração Maven, Java 25, Spotless, Enforcer
├── frontend/                      # Aplicação React 19 + TypeScript
│   ├── src/
│   │   ├── app/                   # Configuração global da aplicação
│   │   ├── components/            # Componentes reutilizáveis de UI
│   │   ├── layouts/               # Estruturas de página (Sidebar, Shell)
│   │   ├── pages/                 # Telas do sistema
│   │   ├── features/              # Módulos funcionais (auth, properties, crm...)
│   │   ├── services/              # Cliente HTTP com Correlation-ID
│   │   ├── hooks/                 # Hooks React customizados
│   │   ├── routes/                # Definição e proteção de rotas
│   │   └── types/                 # Interfaces TypeScript do contrato da API
│   ├── vite.config.ts             # Proxy reverso local para o backend
│   ├── package.json
│   └── .env.example
├── docs/                          # Documentação técnica do projeto
│   ├── Arquitetura_Projeto_SaaS_Imobiliario.docx
│   ├── Plano_Desenvolvimento_SaaS_Imobiliario_Java.docx
│   └── architecture/
│       └── conventions.md         # Convenções de código, camadas e regras
├── infra/                         # Scripts de infraestrutura e banco
│   └── docker/
│       └── postgres/initdb/       # Script SQL/Shell de inicialização (menor privilégio)
├── .editorconfig                  # Padronização de indentação e charset (UTF-8, LF)
├── .gitattributes                 # Normalização de quebras de linha no Git
├── .gitignore                     # Bloqueio de segredos, credenciais e dumps
└── README.md
```

---

## 3. Pré-requisitos de Desenvolvimento

- **Java JDK 25 LTS** (ex: Microsoft Build of OpenJDK 25)
- **Node.js 22 LTS** ou superior e `npm`
- **Docker** e **Docker Compose**
- **Git**

---

## 4. Como Executar Localmente

### Passo 1: Configurar Variáveis de Ambiente

Copie os arquivos de exemplo para criar suas configurações locais (ambos já ignorados pelo Git):

```bash
# No diretório backend/
cp backend/.env.example backend/.env

# No diretório frontend/
cp frontend/.env.example frontend/.env
```

> **Atenção:** No arquivo `backend/.env`, altere as senhas de exemplo para senhas fortes.

---

### Passo 2: Subir o PostgreSQL Local via Docker Compose

Inicie o banco de dados PostgreSQL 18:

```bash
cd backend
docker compose up -d postgres
```

O container configura automaticamente o banco `imovix`, o schema `imovix` e cria um role dedicado `imovix_app` com **menor privilégio** (sem superusuário).

---

### Passo 3: Executar o Backend (Spring Boot)

Com o PostgreSQL ativo:

```bash
cd backend
./mvnw spring-boot:run
```

A API estará acessível em `http://localhost:8080`.

---

### Passo 4: Executar o Frontend (React + Vite)

Em um novo terminal:

```bash
cd frontend
npm install
npm run dev
```

A interface web estará disponível em `http://localhost:5173`, com proxy configurado para encaminhar requisições `/api` diretamente para a API local.

---

## 5. Qualidade de Código, Testes e Convenções

### Backend (Java)

| Ação | Comando |
| :--- | :--- |
| **Formatar código (Google Java Style)** | `./mvnw spotless:apply` |
| **Verificar formatação sem alterar** | `./mvnw spotless:check` |
| **Rodar testes unitários e arquiteturais** | `./mvnw test` |
| **Rodar build completo com integração** | `./mvnw verify` |

- **ArchUnit:** Garante que o domínio não acesse controllers nem JPA, impedindo acoplamento indevido.
- **Fail-Closed Security:** Toda rota HTTP da API é privada por padrão; rotas públicas exigem liberação explícita.
- **CORS Restritivo:** Bloqueia o uso de curingas `*` no startup da aplicação.

### Frontend (React/TypeScript)

| Ação | Comando |
| :--- | :--- |
| **Verificar tipagem e build** | `npm run build` |
| **Executar linter** | `npm run lint` |
| **Servidor de desenvolvimento** | `npm run dev` |

---

## 6. Sequência de Implementação Recomendada

1. [x] **Repositório Git, estrutura backend/frontend, README e convenções de código.**
2. [x] **Configuração de Java 25 LTS, Spring Boot 4, Maven Wrapper, Docker Compose e PostgreSQL local.**
3. [x] **Flyway, health check (Actuator), logs estruturados e tratamento global de erros (RFC 7807).**
4. [x] **Módulos de identidade, empresas e associação de membros.**
5. [x] **Contexto multi-tenant e testes de isolamento de dados.**
6. [x] **CRUD de imóveis com validação, paginação, auditoria e testes.**
7. [x] **Contatos, leads, CRM, funil de vendas (Kanban), agendamento de atividades e visitas.**
   - Entidades e agregados de Lead, LeadActivity e CompanyContact.
   - Quadro Kanban no frontend com alteração de status em tempo real.
   - Agendamento e conclusão de tarefas, ligações, reuniões e visitas imobiliárias.
   - Métricas do funil de conversão e cálculo de taxa de sucesso comercial.
8. [x] **Portal público de anúncios (Listings) e captura direta de leads.**
   - Catálogo público aberto (/api/v1/public/listings) com filtros por tipo, finalidade, faixa de preço e quartos.
   - Formulário de interesse e agendamento de visita público (/api/v1/public/inquiries).
   - Conversão e registro automático do interessado como Lead no CRM do tenant imobiliário.
9. [x] **Planos, assinaturas e controle de limites e cotas do SaaS (subscriptions).**
   - Agregados de Plan e TenantSubscription com status e períodos de faturamento.
   - Tabela de planos pré-configurados (Essencial, Profissional, Enterprise).
   - Controle estrito de cotas com validação de limite excedido (HTTP 403 RFC 7807).
   - Medidores de consumo no frontend para capacidade de imóveis e usuários da equipe.
   - Fluxo interativo de troca e upgrade de planos com persistência e atualização imediata de limites.
10. [x] **Staging, backups/restauração, observabilidade e homologação.**
   - Módulo de auditoria imutável (audit) e conformidade LGPD (/api/v1/audit/events).
   - Observabilidade com métricas de produção expostas via Micrometer Prometheus (/actuator/prometheus).
   - Scripts automatizados de backup e restauração do PostgreSQL (backup-db.sh/ps1 e restore-db.sh/ps1).
   - Pipeline de CI/CD via GitHub Actions (.github/workflows/ci.yml) validando build Java 25, Spotless, ArchUnit e frontend Vite.
