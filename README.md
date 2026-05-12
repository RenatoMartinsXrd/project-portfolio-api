# Project Portfolio API

API para gerenciamento do portfólio de projetos do desafio técnico, implementada com Spring Boot, PostgreSQL e organização em camadas com separação clara entre API REST, regras de negócio e persistência.

## Funcionalidades implementadas

- CRUD completo de projetos
- Atualização de status com validação de fluxo sequencial
- Cancelamento permitido em qualquer etapa
- Cálculo dinâmico de risco com base em orçamento e prazo
- Regra de exclusão por status
- Associação de membros com validações de negócio
- API REST mockada para gerenciamento de membros externos
- Relatório resumido do portfólio
- Paginação e filtros para listagem de projetos
- Segurança básica com autenticação HTTP Basic
- Documentação Swagger/OpenAPI
- Tratamento global de exceções
- Testes unitários com cobertura mínima validada via JaCoCo

## Arquitetura

A aplicação foi organizada em camadas, mantendo separação clara entre controllers, regras de negócio, persistência e integrações, seguindo princípios de Clean Code e SOLID.

Estrutura principal:

- `frameworks/rest` -> controllers REST e DTOs
- `adapters/controllers` -> camada intermediária entre API e regras de negócio
- `adapters/presenters` -> conversão entre DTOs e domínio
- `core/domain` -> entidades, enums e regras de domínio
- `core/usecases` -> regras de negócio e orquestração
- `core/ports` -> contratos de persistência e integrações externas
- `frameworks/jpa` -> entidades JPA, repositories e adapters de persistência
- `shared/config` -> configurações da aplicação
- `shared/exception` -> tratamento global de exceções

## Tecnologias utilizadas

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA / Hibernate
- Spring Security
- PostgreSQL
- Flyway
- Springdoc OpenAPI (Swagger)
- JUnit 5
- JaCoCo
- Maven

## Como executar

### Banco de dados

Subir PostgreSQL via Docker:

```bash
docker compose up -d
```

### Aplicação

```bash
mvn spring-boot:run
```

As migrations do Flyway são executadas automaticamente na inicialização.

## Autenticação

HTTP Basic:

```text
usuario: admin
senha: admin123
```

## Testes e cobertura

Executar testes + validação de cobertura:

```bash
mvn clean verify
```

Relatório JaCoCo:

```text
target/site/jacoco/index.html
```

A cobertura mínima de 70% foi configurada para os pacotes de regras de negócio:

```text
core/domain
core/usecases
```

## Endpoints

### Projetos

```text
POST   /api/v1/projects
GET    /api/v1/projects/{id}
GET    /api/v1/projects
PUT    /api/v1/projects/{id}
DELETE /api/v1/projects/{id}
PATCH  /api/v1/projects/{id}/status
POST   /api/v1/projects/{id}/members/{memberId}
GET    /api/v1/projects/portfolio/report
```

### API externa mockada de membros

```text
POST /api/v1/external/members
GET  /api/v1/external/members
GET  /api/v1/external/members/{id}
```

## Swagger

```text
http://localhost:8080/swagger-ui.html
```

## Fluxo sugerido para validação manual

1. Criar membros via API mockada
2. Criar projeto com gerente responsável
3. Consultar projeto por ID
4. Testar listagem com filtros e paginação
5. Atualizar status seguindo fluxo permitido
6. Validar bloqueio de transição inválida
7. Associar membros funcionários ao projeto
8. Validar bloqueio de associação para membros inválidos
9. Testar exclusão conforme regras de status
10. Gerar relatório resumido do portfólio

## Observações

- A API externa de membros foi mockada no mesmo serviço para simplificar a execução local, mantendo separação lógica dos endpoints.
- O foco da implementação foi atender os requisitos funcionais e regras de negócio propostas no desafio.