# Sistema de Gerenciamento de Biblioteca

Aplicação web full-stack para gerenciamento de livros e locações, com painel administrativo.

## Stack

| Camada   | Tecnologia                                      |
|----------|-------------------------------------------------|
| Backend  | Java 21, Spring Boot 3, Spring Security + JWT   |
| Banco    | SQLite (via Spring Data JPA + Hibernate)         |
| Frontend | Angular 21, Angular Material (M3)               |
| Testes   | JUnit 5 + Mockito (backend); Vitest (frontend)  |

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- Node.js 20+
- Angular CLI (`npm install -g @angular/cli`)

---

## Executar o Backend

```bash
cd backend
mvn spring-boot:run
```

O servidor sobe em **http://localhost:8080**.

Na **primeira execução**, um admin é criado automaticamente e a senha aparece no console:

```
=================================================
Admin account created.
Username: admin
Password: <senha-gerada>
=================================================
```

> A senha é exibida apenas uma vez por banco de dados. Guarde-a antes de limpar o terminal.

### Testes do Backend

```bash
cd backend
mvn test
```

---

## Executar o Frontend

```bash
cd frontend
npm install
ng serve
```

A aplicação fica disponível em **http://localhost:4200**.

### Testes do Frontend

```bash
cd frontend
ng test --watch=false
```

---

## Funcionalidades

| Tela      | O que faz                                                                                      |
|-----------|-----------------------------------------------------------------------------------------------|
| Login     | Autenticação do admin com JWT                                                                 |
| Livros    | Cadastro e edição de livros; filtros client-side por código, nome, autor, ISBN e status       |
| Locações  | Criar locação com autocomplete de clientes (ou cadastro inline); validação mínima de 5 dias   |
| Devolução | Registrar devolução direto na listagem de locações ativas                                     |
| Histórico | Todas as locações encerradas com filtro por livro ou cliente                                  |

---

## API

Base URL: `http://localhost:8080/api`

### Auth
| Método | Endpoint    | Descrição   |
|--------|-------------|-------------|
| POST   | /auth/login | Login admin |

### Livros
| Método | Endpoint    | Descrição                                              |
|--------|-------------|--------------------------------------------------------|
| GET    | /books      | Listar livros (query: name, author, isbn, status, code) |
| POST   | /books      | Cadastrar livro                                        |
| PUT    | /books/{id} | Atualizar livro                                        |

### Clientes
| Método | Endpoint | Descrição                     |
|--------|----------|-------------------------------|
| GET    | /clients | Buscar clientes (query: name) |
| POST   | /clients | Cadastrar cliente             |

### Locações
| Método | Endpoint             | Descrição           |
|--------|----------------------|---------------------|
| GET    | /rentals             | Locações ativas     |
| GET    | /rentals/history     | Histórico completo  |
| POST   | /rentals             | Criar locação       |
| PUT    | /rentals/{id}/return | Registrar devolução |

---

## Regras de Negócio

- **Período mínimo de locação**: 5 dias corridos entre data de início e devolução prevista.
- **Disponibilidade**: livro só pode ser locado com status `AVAILABLE`; ao locar muda para `RENTED`; ao devolver volta para `AVAILABLE`.
- **ISBN**: validado no backend (formato ISBN-10 ou ISBN-13).
- **Código do livro**: gerado automaticamente pelo backend (ex: `LIV-00001`).
- **Histórico**: locações devolvidas nunca são deletadas — `returned_at` marca o encerramento.
