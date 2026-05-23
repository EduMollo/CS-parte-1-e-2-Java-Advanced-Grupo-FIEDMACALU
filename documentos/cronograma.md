# Cronograma de Desenvolvimento — CLYVO VET API

**Disciplina:** Java Advanced — FIAP Challenge 2026  
**Tema:** CLYVO VET — Jornada Contínua de Saúde Pet

---

## Sprint 1 — Entrega: 24/05/2025

### Semana 1 (01/04 – 06/04) — Planejamento e Modelagem
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| Análise dos requisitos do challenge (PDF CLYVO VET Briefing) | Equipe | ✅ Concluído |
| Modelagem do DER (entidades, relacionamentos) | Equipe | ✅ Concluído |
| Definição das regras de negócio (score de risco, alertas) | Equipe | ✅ Concluído |
| Configuração do projeto Spring Boot 3.2.5 + Java 21 | Equipe | ✅ Concluído |

### Semana 2 (07/04 – 13/04) — Entidades e Repositórios
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| Criação das entidades JPA: `Tutor`, `Pet`, `Clinica` | Equipe | ✅ Concluído |
| Criação das entidades JPA: `Consulta`, `EventoSaude`, `Vacina`, `Medicamento` | Equipe | ✅ Concluído |
| Bean Validation em todas as entidades e DTOs | Equipe | ✅ Concluído |
| Repositórios Spring Data JPA + queries JPQL customizadas | Equipe | ✅ Concluído |
| DataLoader com dados de exemplo (H2) | Equipe | ✅ Concluído |

### Semana 3 (14/04 – 20/04) — Serviços e Lógica de Negócio
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| Services: `TutorService`, `PetService`, `ClinicaService` | Equipe | ✅ Concluído |
| Services: `ConsultaService`, `VacinaService`, `MedicamentoService` | Equipe | ✅ Concluído |
| `EventoSaudeService` com alertas proativos | Equipe | ✅ Concluído |
| `SaudePetService` — Dashboard com Score de Risco (Design Pattern: Strategy) | Equipe | ✅ Concluído |
| Cache com `@Cacheable` / `@CacheEvict` nas camadas de serviço | Equipe | ✅ Concluído |

### Semana 4 (21/04 – 27/04) — Controllers e DTOs
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| DTOs Request/Response com fromEntity() para todas as entidades | Equipe | ✅ Concluído |
| Controllers REST: `TutorController`, `PetController`, `ClinicaController` | Equipe | ✅ Concluído |
| Controllers REST: `ConsultaController`, `EventoSaudeController` | Equipe | ✅ Concluído |
| Controllers REST: `VacinaController`, `MedicamentoController` | Equipe | ✅ Concluído |
| Paginação e ordenação com `Pageable` e `@PageableDefault` | Equipe | ✅ Concluído |
| Filtros por parâmetros (`@RequestParam`) em todos os GETs de listagem | Equipe | ✅ Concluído |

### Semana 5 (28/04 – 04/05) — Tratamento de Erros e Documentação
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| `GlobalExceptionHandler` com respostas padronizadas (400, 404, 422, 500) | Equipe | ✅ Concluído |
| `ResourceNotFoundException` e `BusinessException` customizadas | Equipe | ✅ Concluído |
| Configuração SpringDoc OpenAPI 2.5.0 (`@Tag`, `@Operation`) | Equipe | ✅ Concluído |
| Swagger UI acessível em `/swagger-ui.html` | Equipe | ✅ Concluído |

### Semana 6 (05/05 – 11/05) — Testes e Ajustes
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| Testes manuais de todos os endpoints via Postman | Equipe | ✅ Concluído |
| Correção de bugs: tipos (`LocalDate`, `Double`), JPQL, enums inner class | Equipe | ✅ Concluído |
| Coleção Postman completa (`CLYVO_VET_API.postman_collection.json`) | Equipe | ✅ Concluído |
| Validação da inicialização da aplicação e dados de exemplo | Equipe | ✅ Concluído |

### Semana 7 (12/05 – 18/05) — Finalização e Entrega
| Tarefa | Responsável | Status |
|--------|-------------|--------|
| README.md com arquitetura, rotas, DER e instruções de execução | Equipe | ✅ Concluído |
| Cronograma de desenvolvimento | Equipe | ✅ Concluído |
| Revisão final dos Design Patterns (Strategy, Repository, Builder, DTO) | Equipe | ✅ Concluído |
| Testes de regressão — compilação limpa, startup em < 10s | Equipe | ✅ Concluído |
| Submissão no GitHub / FIAP | Equipe | ✅ Concluído |

---

## Tecnologias Utilizadas por Sprint

| Sprint | Tecnologia / Recurso |
|--------|----------------------|
| 1 | Spring Boot 3.2.5, Java 21, H2, Lombok |
| 2 | Spring Data JPA, Hibernate, Bean Validation, JPQL |
| 3 | Spring Cache (`@Cacheable`), Design Pattern Strategy |
| 4 | Spring MVC (REST), Pageable, DTOs, HATEOAS-ready |
| 5 | SpringDoc OpenAPI, GlobalExceptionHandler |
| 6–7 | Postman, Testes manuais, README, Cronograma |

---

## Requisitos Java Advanced Implementados

| Requisito | Implementado | Onde |
|-----------|-------------|------|
| Bean Validation | ✅ | Models + DTOs (Request) |
| Paginação | ✅ | Todos os GETs de listagem |
| Ordenação | ✅ | `@PageableDefault(sort=...)` |
| Parâmetros de busca | ✅ | `@RequestParam` + JPQL com filtros |
| Cache | ✅ | `@Cacheable` / `@CacheEvict` em Services |
| Tratamento de Erros | ✅ | `GlobalExceptionHandler` + exceptions customizadas |
| DTOs | ✅ | Request/Response com `fromEntity()` |
| Swagger / OpenAPI | ✅ | SpringDoc 2.5.0 em `/swagger-ui.html` |
| JPQL | ✅ | Repositories com `@Query` customizadas |
| Design Patterns | ✅ | Strategy (SaudePetService), Repository, Builder, DTO |
| RESTful além do CRUD | ✅ | Dashboard, timeline, score de risco, alertas proativos |
| Postman Collection | ✅ | `postman/CLYVO_VET_API.postman_collection.json` |
