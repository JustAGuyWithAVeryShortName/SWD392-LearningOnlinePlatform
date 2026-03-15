# Backend Architecture Summary

## 1. System Overview
This project is a **Spring Boot 3.5.3** monolith for an English e-learning platform.

- Language/runtime: Java 21
- Build tool: Maven
- Frameworks: Spring Web, Spring Data JPA, Spring Security, Validation, WebSocket, Mail
- API docs: springdoc OpenAPI/Swagger
- Database: Microsoft SQL Server (via `mssql-jdbc`)
- Main entry point: `SharedEnglishELearningPathApplication`

The codebase follows a classic layered architecture with REST + WebSocket endpoints and several external service integrations.

## 2. High-Level Architecture

```text
Clients (Web app, API consumers)
  |
  | HTTP/JSON + STOMP/WebSocket
  v
Controllers + WebSocket endpoints
  |
  v
Services (business rules, orchestration)
  |
  v
Repositories (Spring Data JPA)
  |
  v
SQL Server database

Cross-cutting: Security (JWT/OAuth2), Validation, Exception handling, DTO mapping
Integrations: Google OAuth2/Calendar/Sheets, Cloudinary, MoMo payment, SMTP mail
```

## 3. Package Structure and Responsibilities
Main package: `com.hsp302.shared_english_e_learning_path`

- `config`: Security, JWT decoder, CORS, WebSocket broker, Cloudinary/Google/Jackson setup.
- `controllers`: HTTP API endpoints per domain (`AuthenticationController`, `CourseController`, `VideoController`, etc.).
- `services`: Business logic and integration orchestration (`AuthenticationService`, `MomoPaymentService`, `CloudinaryVideoService`, etc.).
- `repositories`: Persistence interfaces extending `JpaRepository`.
- `domain.entities`: JPA entities (`User`, `Course`, `Lesson`, `Enrollment`, `Video`, etc.).
- `domain.dtos` and `domain.dto`: Request/response contracts for API boundaries.
- `mappers`: MapStruct-based DTO/entity conversion.
- `security`: JWT filter, user details, and WebSocket interceptors.
- `exception`: Global exception handling and custom exception classes.
- `validators`, `utils`, `context`: Supporting infrastructure and shared helpers.

## 4. Layered Request Flow
Typical REST request flow:

1. Client calls a controller endpoint.
2. Security filter chain validates token (unless endpoint is public).
3. Controller delegates to a service.
4. Service applies business logic and calls repositories/integrations.
5. Repository executes DB operations through JPA.
6. Service maps entities to DTOs.
7. Controller returns response (`ResponseEntity`).
8. Errors are normalized by `GlobalExceptionHandler` into `ApiResponse`.

## 5. Security Architecture
Security is centralized in `SecurityConfig` and related classes.

- Auth model:
  - Custom JWT token creation/verification in `AuthenticationService`.
  - JWT request authentication via `JwtAuthenticationFilter`.
  - OAuth2 login (Google) with token handoff to frontend.
- Authorization:
  - Public endpoint allowlists by HTTP method.
  - Remaining routes require authentication.
  - Method-level security enabled (`@EnableMethodSecurity`).
- Resource server:
  - Uses custom decoder and JWT authority mapping from `scope` claim with `ROLE_` prefix.
- CORS:
  - Currently configured for `http://localhost:5173`.

## 6. Real-Time Messaging (WebSocket)
`WebSocketConfig` enables STOMP messaging:

- Endpoint: `/ws` (SockJS + native WebSocket)
- Broker destinations: `/topic`, `/queue`, `/user`
- App destination prefix: `/app`
- User destination prefix: `/user`
- Inbound authentication is enforced by `WebSocketAuthInterceptor`

This supports chat and user-targeted real-time events.

## 7. Data and Domain Model
The platform persists multiple learning and operations domains:

- Learning: `Course`, `Module`, `Lesson`, `Quiz`, `QuizOption`, `QuizSubmission`, `Progress`, `Enrollment`
- User/identity: `User`, `Password`, `InvalidatedToken`, `Qualification`
- Communication/content: `Blog`, `ChatRoom`, `ChatMessage`, `Video`
- Operations: `Appointment`, `Availability`, `Payment`, `StaffRequest`

Repositories are Spring Data interfaces, with method-name queries and occasional custom `@Query` usage.

## 8. Integration Architecture
External integrations are implemented mostly in service/config classes:

- Google OAuth2 login (Spring Security OAuth2 client)
- Google Calendar / Sheets APIs
- Cloudinary media hosting
- MoMo payment gateway (IPN + callback flow)
- SMTP email via Spring Mail

These integrations are orchestrated in the service layer so controllers remain thin.

## 9. Error Handling and API Consistency
`GlobalExceptionHandler` (`@RestControllerAdvice`) centralizes exception-to-response mapping.

- Converts common exceptions (`EntityNotFoundException`, `BadCredentialsException`, validation exceptions, and domain-specific exceptions) into structured HTTP responses.
- Keeps controllers/services focused on domain logic while preserving consistent API error formats.

## 10. Configuration and Runtime
Key runtime configuration is in `application.properties`:

- Data source + JPA/Hibernate settings
- JWT settings (secret, issuer, lifetimes)
- OAuth2 client/provider settings
- Mail server settings
- Cloudinary, Google API, and MoMo settings
- Multipart upload limits

## 11. Testing and Build
- Maven wrapper scripts included (`mvnw`, `mvnw.cmd`)
- Test dependencies: Spring Boot Test, Spring Security Test
- Unit/integration tests are under `src/test/java`

## 12. Architectural Strengths
- Clear layered separation (controller/service/repository)
- Cross-cutting concerns handled centrally (security, exception mapping)
- DTO + mapper approach reduces API/domain coupling
- Supports both synchronous APIs and asynchronous real-time messaging
- Integration-ready design for media, payments, and Google services

## 13. Improvement Opportunities
- Externalize secrets to environment variables or a secrets manager (do not keep credentials in source properties).
- Use password hashing consistently (BCrypt) for all authentication paths.
- Consider profile-based configuration (`dev`, `staging`, `prod`) for safer deployment defaults.
- Expand automated tests around security and integration boundaries.
- Add architecture decision records (ADRs) and C4 diagrams for long-term maintainability.
