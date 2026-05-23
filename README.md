# CLYVO VET API — FIAP Challenge 2026

> **Grupo:** FIEDMACALU
> **Disciplina:** Java Advanced  
> **Sprint 1 e 2 — Entrega:** 24/05/2025  
> **Repositório:** [CS-parte-1-e-2-Java-Advanced-Grupo-FIEDMACALU](https://github.com/EduMollo/CS-parte-1-e-2-Java-Advanced-Grupo-FIEDMACALU)

---

## Sobre o Projeto

A **CLYVO VET** é uma plataforma digital de saúde pet que propõe uma **jornada contínua de cuidado**, conectando tutores, pets e clínicas veterinárias em um ecossistema integrado.

Este projeto implementa a **API RESTful** do backend da plataforma, desenvolvida com **Java 21 + Spring Boot 3.2.5**.

---

## Tecnologias

| Tecnologia          | Versão  | Finalidade                          |
|---------------------|---------|-------------------------------------|
| Java                | 21      | Linguagem principal                 |
| Spring Boot         | 3.2.5   | Framework principal                 |
| Spring Data JPA     | —       | Persistência, JPQL                  |
| H2 Database         | —       | Banco em memória (dev + testes)     |
| Spring Cache        | —       | Cache simples                       |
| Bean Validation     | —       | Validação de entrada                |
| SpringDoc OpenAPI   | 2.5.0   | Documentação Swagger                |
| Lombok              | —       | Redução de boilerplate              |

---

## Arquitetura

```
src/main/java/br/com/fiap/clyvovet/
├── config/          # DataLoader, OpenApiConfig
├── controller/      # REST Controllers (6 controllers)
├── dto/             # Request/Response DTOs + SaudePetDashboard
├── exception/       # GlobalExceptionHandler, BusinessException, ResourceNotFoundException
├── model/           # Entidades JPA (Tutor, Pet, Clinica, Consulta, EventoSaude, Vacina, Medicamento)
├── repository/      # Spring Data JPA + JPQL customizados
└── service/         # Lógica de negócio + Design Patterns
```

### Design Patterns aplicados

- **Strategy** — `SaudePetService`: cálculo de score de risco com regras intercambiáveis
- **Repository** — Spring Data JPA abstrai acesso a dados
- **Builder** — Lombok `@Builder` em todas as entidades e DTOs
- **DTO Pattern** — Separação clara entre camada de domínio e API

---

## Diagrama de Entidades (DER simplificado)

```
Tutor (1) ──< (N) Pet
Pet   (N) >── (1) Clinica  [clinicaPrincipal]
Pet   (1) ──< (N) Consulta
Pet   (1) ──< (N) EventoSaude
Pet   (1) ──< (N) Vacina
Pet   (1) ──< (N) Medicamento
Clinica (1) ──< (N) Consulta
Consulta (1) ──< (N) EventoSaude
```

---

## Como Executar

### Pré-requisitos
- Java 21+
- **Nenhum banco de dados externo necessário** — usa H2 in-memory

### Rodar a aplicação (Windows)

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação iniciará em `http://localhost:8080`

### Acessos úteis

| Recurso       | URL                                      |
|---------------|------------------------------------------|
| Swagger UI    | http://localhost:8080/swagger-ui.html    |
| API Docs JSON | http://localhost:8080/api-docs           |
| H2 Console    | http://localhost:8080/h2-console         |

**H2 Console config:**
- JDBC URL: `jdbc:h2:mem:clyvovetdb`
- User: `sa`
- Password: *(em branco)*

---

## Endpoints da API

### Tutores — `/api/v1/tutores`

| Método | Endpoint                    | Descrição                                    |
|--------|-----------------------------|----------------------------------------------|
| GET    | `/`                         | Listar tutores (filtro: nome, email; paginado)|
| GET    | `/{id}`                     | Buscar tutor por ID                          |
| GET    | `/cpf/{cpf}`                | Buscar tutor por CPF                         |
| POST   | `/`                         | Cadastrar novo tutor                         |
| PUT    | `/{id}`                     | Atualizar tutor                              |
| DELETE | `/{id}`                     | Inativar tutor (soft delete)                 |

### Pets — `/api/v1/pets`

| Método | Endpoint                    | Descrição                                          |
|--------|-----------------------------|-----------------------------------------------------|
| GET    | `/`                         | Listar pets (filtro: nome, especie, raca; paginado) |
| GET    | `/{id}`                     | Buscar pet por ID                                  |
| GET    | `/tutor/{tutorId}`          | Listar pets de um tutor                            |
| GET    | `/{id}/dashboard`           | Dashboard de saúde com score de risco              |
| POST   | `/`                         | Cadastrar novo pet                                 |
| PUT    | `/{id}`                     | Atualizar pet                                      |
| DELETE | `/{id}`                     | Inativar pet (soft delete)                         |

### Clínicas — `/api/v1/clinicas`

| Método | Endpoint | Descrição                                              |
|--------|----------|--------------------------------------------------------|
| GET    | `/`      | Listar clínicas (filtro: nome, cidade, estado; paginado)|
| GET    | `/{id}`  | Buscar clínica por ID                                  |
| POST   | `/`      | Cadastrar nova clínica                                 |
| PUT    | `/{id}`  | Atualizar clínica                                      |
| DELETE | `/{id}`  | Inativar clínica (soft delete)                         |

### Consultas — `/api/v1/consultas`

| Método | Endpoint                        | Descrição                               |
|--------|---------------------------------|-----------------------------------------|
| GET    | `/pet/{petId}`                  | Listar consultas de um pet (paginado)   |
| GET    | `/pet/{petId}/historico`        | Histórico completo ordenado             |
| GET    | `/clinica/{clinicaId}`          | Listar consultas de uma clínica         |
| GET    | `/status/{status}`              | Filtrar por status                      |
| GET    | `/retornos-pendentes`           | Retornos vencidos ou de hoje            |
| GET    | `/{id}`                         | Buscar consulta por ID                  |
| POST   | `/`                             | Agendar nova consulta                   |
| PUT    | `/{id}`                         | Atualizar consulta                      |
| PATCH  | `/{id}/cancelar`                | Cancelar consulta                       |

### Eventos de Saúde — `/api/v1/eventos-saude`

| Método | Endpoint                        | Descrição                                   |
|--------|---------------------------------|---------------------------------------------|
| GET    | `/pet/{petId}`                  | Listar eventos de um pet (paginado)         |
| GET    | `/pet/{petId}/timeline`         | Timeline completa de saúde                  |
| GET    | `/pet/{petId}/alertas`          | Alertas pendentes do pet                    |
| GET    | `/tutor/{tutorId}/alertas`      | Alertas de todos os pets do tutor           |
| POST   | `/`                             | Registrar novo evento                       |
| PATCH  | `/{id}/concluir`                | Marcar evento como concluído                |

### Vacinas — `/api/v1/vacinas`

| Método | Endpoint                        | Descrição                                   |
|--------|---------------------------------|---------------------------------------------|
| GET    | `/pet/{petId}`                  | Listar vacinas de um pet (paginado)         |
| GET    | `/pet/{petId}/pendentes`        | Vacinas com próxima dose pendente           |
| GET    | `/tutor/{tutorId}/pendentes`    | Vacinas pendentes de todos os pets do tutor |
| POST   | `/`                             | Registrar nova vacina                       |

### Medicamentos — `/api/v1/medicamentos`

| Método | Endpoint                        | Descrição                                   |
|--------|---------------------------------|---------------------------------------------|
| GET    | `/pet/{petId}`                  | Listar medicamentos de um pet (paginado)    |
| GET    | `/pet/{petId}/ativos`           | Medicamentos ativos no período atual        |
| GET    | `/pet/{petId}/uso-continuo`     | Medicamentos de uso contínuo ativos         |
| POST   | `/`                             | Registrar novo medicamento                  |

---

## Recursos Técnicos Implementados

- ✅ **OOP** — Herança, encapsulamento, polimorfismo nas entidades e serviços
- ✅ **JPA / Hibernate** — Mapeamento ORM completo, relacionamentos One/Many
- ✅ **RESTful além do CRUD** — Dashboard, timeline, alertas, retornos pendentes, score de risco
- ✅ **Bean Validation** — `@NotBlank`, `@CPF`, `@Email`, `@Past`, `@Size`, `@Digits` nos DTOs
- ✅ **Paginação e Ordenação** — `Pageable` com `@PageableDefault` em todos os GETs de listagem
- ✅ **Parâmetros de busca** — `@RequestParam` para filtros por nome, cidade, estado, espécie, etc.
- ✅ **Cache** — `@Cacheable` e `@CacheEvict` nas camadas de serviço
- ✅ **Tratamento de erros** — `GlobalExceptionHandler` com respostas padronizadas
- ✅ **DTOs** — Request e Response separados, nunca expõe entidades diretamente
- ✅ **Swagger / OpenAPI** — Documentação automática em `/swagger-ui.html`
- ✅ **Design Patterns** — Strategy, Repository, Builder, DTO
- ✅ **JPQL** — Queries customizadas com filtros dinâmicos e joins
- ✅ **Spring JPA Query Methods** — findBy, existsBy, countBy

---

## Dados de Exemplo

Ao iniciar, o `DataLoader` carrega automaticamente:
- **2 clínicas** (SP e RJ)
- **3 tutores**
- **5 pets** (2 cães Golden/Labrador/Poodle, 2 gatos Persa/Maine Coon — variedade de espécies)
- Consultas, vacinas, medicamentos e eventos de saúde associados

---

## Equipe

| Nome | RM |
|------|----|
| *Carlos Alberto Guedes* | *RM: 566022* |
| *Eduardo Novaes Mollo* | *RM: 561515* |
| *Filippo Dal Medico Tolone* | *RM: 562329* |
| *Luan Peixoto Marins* | *RM: 562258* |
| *Mathaus Victor Souza Marcelino* | *RM: 564146* |
