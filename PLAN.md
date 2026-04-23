# PLAN.md — Sistema de Gerenciamento de Biblioteca

## Visão Geral

Sistema web full-stack para gerenciamento de livros e locações, com painel administrativo. O backend segue Clean Architecture + DDD e o frontend usa Angular com Angular Material.

---

## Stack Tecnológica

| Camada     | Tecnologia                          |
|------------|-------------------------------------|
| Backend    | Java 21, Spring Boot 3.x            |
| Banco      | SQLite (via Spring Data JPA + Hibernate) |
| Frontend   | Angular 17+, Angular Material       |
| Testes     | JUnit 5, Mockito (backend); Jasmine/Karma (frontend) |
| Build      | Maven (backend), Angular CLI (frontend) |

---

## Arquitetura Backend (Clean Architecture + DDD)

```
backend/
├── src/main/java/com/biblioteca/
│   ├── domain/
│   │   ├── model/          # Entidades e Value Objects
│   │   │   ├── book/
│   │   │   │   ├── Book.java
│   │   │   │   ├── BookStatus.java (enum: AVAILABLE, RENTED, MAINTENANCE)
│   │   │   │   └── ISBN.java (value object)
│   │   │   ├── client/
│   │   │   │   └── Client.java
│   │   │   └── rental/
│   │   │       ├── Rental.java
│   │   │       └── RentalPeriod.java (value object, min 5 days)
│   │   └── repository/     # Interfaces (ports)
│   │       ├── BookRepository.java
│   │       ├── ClientRepository.java
│   │       └── RentalRepository.java
│   ├── application/
│   │   └── usecase/        # Casos de uso
│   │       ├── auth/
│   │       │   └── AuthenticateAdminUseCase.java
│   │       ├── book/
│   │       │   ├── RegisterBookUseCase.java
│   │       │   └── SearchBooksUseCase.java
│   │       ├── client/
│   │       │   └── RegisterClientUseCase.java
│   │       └── rental/
│   │           ├── CreateRentalUseCase.java
│   │           └── ReturnBookUseCase.java
│   ├── infrastructure/
│   │   ├── persistence/    # Implementações JPA
│   │   │   ├── BookJpaRepository.java
│   │   │   ├── ClientJpaRepository.java
│   │   │   └── RentalJpaRepository.java
│   │   └── security/
│   │       ├── JwtTokenProvider.java
│   │       └── SecurityConfig.java
│   └── presentation/
│       └── controller/     # REST Controllers
│           ├── AuthController.java
│           ├── BookController.java
│           ├── ClientController.java
│           └── RentalController.java
```

---

## Arquitetura Frontend (Angular)

```
frontend/
├── src/app/
│   ├── core/
│   │   ├── auth/
│   │   │   ├── auth.service.ts
│   │   │   ├── auth.guard.ts
│   │   │   └── jwt.interceptor.ts
│   │   └── models/
│   │       ├── book.model.ts
│   │       ├── client.model.ts
│   │       └── rental.model.ts
│   ├── features/
│   │   ├── login/
│   │   │   ├── login.component.ts
│   │   │   └── login.component.html
│   │   ├── books/
│   │   │   ├── book-list/          # Tabela com filtros
│   │   │   └── book-form/          # Sidesheet de cadastro
│   │   └── rentals/
│   │       └── rental-form/        # Modal de locação
│   └── shared/
│       └── components/
```

---

## Modelo de Dados (SQLite)

### `admin`
| Campo      | Tipo    | Observação                        |
|------------|---------|-----------------------------------|
| id         | INTEGER | PK                                |
| username   | TEXT    | único                             |
| password   | TEXT    | bcrypt hash                       |

### `book`
| Campo      | Tipo    | Observação                                        |
|------------|---------|---------------------------------------------------|
| id         | INTEGER | PK                                                |
| code       | TEXT    | único por exemplar, gerado automaticamente        |
| name       | TEXT    |                                                   |
| author     | TEXT    |                                                   |
| isbn       | TEXT    | não único — múltiplos exemplares do mesmo ISBN    |
| status     | TEXT    | AVAILABLE / RENTED / MAINTENANCE                  |

### `client`
| Campo      | Tipo    | Observação                        |
|------------|---------|-----------------------------------|
| id         | INTEGER | PK                                |
| name       | TEXT    |                                   |
| email      | TEXT    | único                             |
| phone      | TEXT    |                                   |

### `rental`
| Campo        | Tipo    | Observação                      |
|--------------|---------|---------------------------------|
| id           | INTEGER | PK                              |
| book_id      | INTEGER | FK → book                       |
| client_id    | INTEGER | FK → client                     |
| start_date   | DATE    |                                 |
| end_date     | DATE    | mínimo start_date + 5 dias      |
| returned_at  | DATE    | nullable                        |

---

## API REST

### Auth
| Método | Endpoint         | Descrição                   |
|--------|------------------|-----------------------------|
| POST   | /api/auth/login  | Login admin → retorna JWT   |

### Livros
| Método | Endpoint         | Descrição                            |
|--------|------------------|--------------------------------------|
| GET    | /api/books       | Listar/buscar (query params: name, author, isbn, status, code) |
| POST   | /api/books       | Cadastrar livro                      |
| GET    | /api/books/{id}  | Buscar por ID                        |
| PUT    | /api/books/{id}  | Atualizar livro                      |

### Clientes
| Método | Endpoint          | Descrição                                              |
|--------|-------------------|--------------------------------------------------------|
| GET    | /api/clients      | Buscar clientes (query param: name, email) — autocomplete |
| POST   | /api/clients      | Cadastrar cliente (chamado inline no modal de locação) |

### Locações
| Método | Endpoint                 | Descrição                                        |
|--------|--------------------------|--------------------------------------------------|
| POST   | /api/rentals             | Criar locação (valida período mínimo)            |
| PUT    | /api/rentals/{id}/return | Registrar devolução (na listagem de locações)    |
| GET    | /api/rentals             | Listar locações ativas                           |
| GET    | /api/rentals/history     | Histórico completo (filtro por livro ou cliente) |

---

## Regras de Negócio

1. **Senha do admin** — gerada aleatoriamente (16+ chars, letras + números + símbolos) no primeiro boot via `CommandLineRunner`, impressa no console e salva com bcrypt.
2. **Período mínimo de locação** — `end_date` deve ser pelo menos `start_date + 5 dias`; validado no domain (`RentalPeriod`).
3. **Disponibilidade** — livro só pode ser locado se `status = AVAILABLE`; ao locar, muda para `RENTED`; ao devolver, volta para `AVAILABLE`.
4. **ISBN** — validado como value object no domain (formato ISBN-10 ou ISBN-13); múltiplos exemplares podem compartilhar o mesmo ISBN.
5. **Busca** — suporta filtro simultâneo por: nome, autor, ISBN, código, status.
6. **Cadastro de cliente** — feito inline no modal de locação; campo de autocomplete busca por nome ou e-mail; se não encontrar, exibe mini-form para cadastro imediato.
7. **Histórico** — locações encerradas (`returned_at` preenchido) são mantidas no banco; endpoint `/api/rentals/history` permite filtrar por `bookId` ou `clientId`.

---

## Fases de Implementação

### Fase 1 — Backend Core
- [ ] Setup Spring Boot + SQLite + JPA
- [ ] Entidades e value objects do domain
- [ ] Repositories (interfaces + implementações JPA)
- [ ] Use cases: auth, cadastro de livro, busca de livros
- [ ] AuthController + BookController
- [ ] Configuração JWT + Spring Security
- [ ] Geração de senha do admin no boot
- [ ] Testes unitários dos use cases

### Fase 2 — Backend Locações e Clientes
- [ ] Entidades Client e Rental
- [ ] Use cases: cadastro de cliente, criar locação, devolver livro
- [ ] ClientController + RentalController
- [ ] Validação de período mínimo (5 dias)
- [ ] Testes unitários

### Fase 3 — Frontend Base
- [ ] Setup Angular + Angular Material + routing
- [ ] Tela de login (reactive form + AuthService + JWT interceptor)
- [ ] AuthGuard para rotas protegidas

### Fase 4 — Frontend Livros
- [ ] Tela de consulta com `MatTable` + filtros por coluna (client-side)
- [ ] Sidesheet (MatDrawer) para cadastro de livro
- [ ] Coluna de ações com botão "Locar"

### Fase 5 — Frontend Locações
- [ ] Modal (MatDialog) de locação: autocomplete de cliente + mini-form inline de cadastro
- [ ] Seleção de datas com validação de período mínimo (5 dias)
- [ ] Listagem de locações ativas com botão "Devolver" por linha
- [ ] Aba/seção de histórico de locações com filtro por livro ou cliente

### Fase 6 — Polimento
- [ ] Tratamento global de erros (backend: `@ControllerAdvice`; frontend: interceptor)
- [ ] Loading states e empty states na tabela
- [ ] Revisão de testes
- [ ] README com instruções de execução

---

## Decisões Tomadas (Q&A)

| # | Questão | Resposta |
|---|---------|----------|
| 1 | Cadastro de clientes | Inline no modal de locação; autocomplete busca existente, mini-form cria novo |
| 2 | Paginação | Client-side |
| 3 | Múltiplas cópias | Permitido — mesmo ISBN pode ter N exemplares, cada um com código único |
| 4 | Devolução | Na mesma tela de locações, botão "Devolver" por linha |
| 5 | Histórico | Sim — histórico completo com filtro por livro ou cliente |

---

## Decisões de Design

- **JWT stateless** — sem sessão server-side; token com expiração configurável.
- **SQLite** — arquivo único `biblioteca.db` na raiz do projeto; sem necessidade de servidor de banco externo.
- **Código do livro** — gerado automaticamente pelo backend (ex: `LIV-00001`) por exemplar, garantindo unicidade mesmo com múltiplas cópias do mesmo ISBN.
- **Sidesheet vs Dialog** — cadastro de livro usa `MatDrawer` (sidesheet); locação usa `MatDialog` com autocomplete de cliente e mini-form inline.
- **Histórico** — locações devolvidas nunca são deletadas; `returned_at` indica encerramento. Endpoint separado `/api/rentals/history` evita poluir a listagem de locações ativas.
