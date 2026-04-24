# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Full-stack library management system (biblioteca) — Java/Spring Boot backend + Angular 21 frontend in a monorepo.

## Commands

### Backend (from `backend/`)
```bash
mvn spring-boot:run          # Dev server at http://localhost:8080
mvn test                     # All tests
mvn test -Dtest=ClassName    # Single test class
mvn test -Dtest=ClassName#methodName  # Single test method
mvn clean install            # Full build
```

### Frontend (from `frontend/`)
```bash
ng serve                     # Dev server at http://localhost:4200
ng test --watch=false        # Run all tests once (Vitest)
ng build                     # Production build → dist/frontend
npx prettier --write src/    # Format code
```

## Architecture

### Backend — Clean Architecture + DDD

Dependency direction: `presentation → application → domain ← infrastructure`

| Layer | Package | Role |
|-------|---------|------|
| Domain | `domain/model/` | Entities + Value Objects (no framework deps) |
| Domain | `domain/repository/` | Repository interfaces (ports) |
| Application | `application/usecase/` | Use cases with `Input` record + `execute()` |
| Infrastructure | `infrastructure/persistence/` | JPA adapters implementing domain repos |
| Infrastructure | `infrastructure/security/` | JWT + Spring Security config |
| Presentation | `presentation/controller/` | REST controllers (Auth, Book, Client, Rental) |
| Presentation | `presentation/dto/` | Request/Response DTOs |
| Presentation | `presentation/handler/` | Global exception handler |

**Use case convention:** each use case is a `@Service` class with an inner `Input` record and an `execute(Input)` method.

**Value Objects:** `ISBN` (validates ISBN-10/13), `RentalPeriod` (enforces 5-day minimum), `BookStatus` (AVAILABLE ↔ RENTED).

**Database:** SQLite via JPA/Hibernate. File auto-created at project root as `biblioteca.db`. Admin account is auto-generated on first boot — password printed to console once.

### Frontend — Angular 21

```
core/        — auth services, guards, JWT interceptor, error interceptor, models
features/    — books/, rentals/, login/ (standalone components)
shared/      — shell layout
```

- Standalone components with Angular Material M3
- Services use signals (`computed` + readonly) for reactive state
- HTTP interceptors handle JWT injection (`auth.interceptor`) and global errors (401, 5xx, network) (`error.interceptor`)
- Vitest for unit tests (not Jasmine/Karma)

## Key Business Rules

- Rentals require a minimum 5-day period (enforced in `RentalPeriod` value object)
- Book codes are auto-generated as `LIV-00001` format
- ISBN must be 10 or 13 digits
- Books are soft-deleted (rental history preserved)
- JWT tokens expire in 24 hours; auth is stateless

## Stack

- Java 21, Spring Boot 3.2.5, Spring Security + JJWT 0.12.5
- SQLite 3.45.1.0 + Hibernate 6.4.4 + Spring Data JPA
- Angular 21.2, Angular Material 21.2 (M3), TypeScript strict mode
- JUnit 5 + Mockito (backend), Vitest (frontend)
