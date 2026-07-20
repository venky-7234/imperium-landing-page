# Enterprise Architecture (Version 1.0)

## Design Philosophy
The Imperium Invitation Management System transitioned from a rapid-prototyping Feature-Based Architecture to a strictly enforced **Layered Architecture**. This ensures separation of concerns, strict dependency rules, and high maintainability for enterprise teams.

## 1. Backend Architecture (Spring Boot)

The backend follows traditional N-Tier architecture principles.

### Packaging Structure
- `com.imperium.ims.controller`: REST APIs handling HTTP requests and routing.
- `com.imperium.ims.service`: Business logic interfaces.
- `com.imperium.ims.service.impl`: Concrete business logic implementations.
- `com.imperium.ims.repository`: Spring Data JPA interfaces for database access.
- `com.imperium.ims.entity`: Hibernate `@Entity` classes representing table schemas.
- `com.imperium.ims.dto`: Data Transfer Objects for API contracts.
- `com.imperium.ims.mapper`: MapStruct interfaces for converting between Entities and DTOs.
- `com.imperium.ims.security`: JWT filters, Authentication providers, and Web Security configs.
- `com.imperium.ims.exception`: Global exception handlers and custom error definitions.
- `com.imperium.ims.config`: Spring configuration beans (e.g., OpenAPI, CORS).
- `com.imperium.ims.util`: Reusable helper classes.

### Core Patterns
- **DTO Pattern**: Entities never cross the controller boundary.
- **Dependency Injection**: Constructor-based injection via Lombok (`@RequiredArgsConstructor`).
- **Global Exception Handling**: `@ControllerAdvice` transforms raw exceptions into standard `ApiResponse` JSON objects.

## 2. Frontend Architecture (React + Vite)

The frontend organizes code by technical layer and routing scope.

### Directory Structure
- `src/pages/`: Next.js-style routing structure mapping to actual views.
- `src/components/common/`: Reusable UI elements (buttons, modals).
- `src/components/dashboard/`: Complex logical components used specifically in the admin dashboard.
- `src/components/forms/`: Centralized forms and validation logic.
- `src/layouts/`: Context providers and ambient wrappers.
- `src/services/`: API integration and data fetching logic.

## 3. Database Architecture (MySQL + Flyway)
- **Schema Management**: Flyway handles all structural changes. Migrations are strictly versioned (e.g., `V1__init.sql`, `V32__production_cleanup.sql`).
- **Security**: Soft deletes and audit logs ensure tracking of sensitive operations. No production data is manipulated outside of the API.
