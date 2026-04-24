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

## Executar com Docker (recomendado)

**Pré-requisito:** Docker e Docker Compose instalados.

```bash
docker compose up --build
```

| Serviço   | URL                       |
|-----------|---------------------------|
| Frontend  | http://localhost          |
| Backend   | http://localhost:8080     |

Na **primeira execução**, o admin é criado automaticamente e a senha aparece nos logs do backend:

```
=================================================
Admin account created.
Username: admin
Password: <senha-gerada>
=================================================
```

Para ver os logs em tempo real:

```bash
docker compose logs -f backend
```

> O banco de dados SQLite é persistido em um volume Docker (`db_data`). A senha só aparece uma vez — guarde antes de fechar o terminal.

Para parar e remover os containers:

```bash
docker compose down
```

Para apagar também o banco de dados:

```bash
docker compose down -v
```

---

## Desenvolvimento Local

**Pré-requisitos:** Java 21+, Maven 3.9+, Node.js 20+, Angular CLI.

### Backend

```bash
cd backend
mvn spring-boot:run
```

Sobe em **http://localhost:8080**.

### Frontend

```bash
cd frontend
npm install
ng serve
```

Disponível em **http://localhost:4200**. O Angular CLI já está configurado com proxy para repassar chamadas `/api` ao backend local.

### Testes

```bash
# Backend (JUnit 5 + Mockito + JaCoCo)
cd backend && mvn test

# Frontend (Vitest)
cd frontend && ng test --watch=false
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
