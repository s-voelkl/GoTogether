# GoTogether Architecture

## Drei-Schichten-Architektur

Die Architektur folgt dem MVP und konzentriert sich auf die Kern-Use-Cases aus den Abschnitten MVP, Inhaltliche User Stories, Technische User Stories und Unternehmensbezogene User Stories.

**Presentation Tier:** Die React-Native-App zeigt die offene Challenge-Liste, die Echtzeitkarte, Social-Battery, Matching und den Reward-Status. Sie kapselt nur die UI und kommuniziert über die API mit dem Backend.

**Application Tier:** Das Java Springboot mit REST-API Schnittstelle als Backend setzt die MVP-Logik um: Challenge-Filter, Kartenabfragen, Social-Battery-Filter, Interessen-Matching, QR-Check-in, Belohnungen und Unternehmens-APIs für Events oder Challenges.

**Data Tier:** PostgreSQL speichert die dafür nötigen Daten wie Nutzer, Profile, Interessen, Quests, Events, Check-ins, Freundschaften, Rewards und Unternehmensprofile.

## Repository Structure

- `src/backend`: Java Springboot Backend mit REST-API
  - `controller`: REST-Controller für Endpunkte
  - `services`: Geschäftslogik für Use Cases
  - `model`: JPA-Entities und Embeddables für DB
  - `repository`: JPA-Repositories für DB-Zugriff
  - `dto`: Data Transfer Objects für API-Kommunikation
- `src/frontend`: React-Native App
  - `components`: Wiederverwendbare UI-Komponenten
  - `screens`: Hauptbildschirme der App
  - `services`: API-Client und Logik für Frontend
  - `assets`: Bilder, Icons, Styles

## Backend-Architecture

The backend is a Spring Boot application split into the standard layers: controllers expose REST endpoints, services contain the business logic, mappers translate between entities and DTOs, and JPA entities persist to PostgreSQL. All identifiers are `UUID`s generated with `GenerationType.UUID`, and controllers consistently return `ResponseEntity<?>` while mapping `RuntimeException`s to HTTP status codes.

### Database and Models

JPA entities and embeddables live in the [model](src/backend/src/main/java/com/gotogether/backend/model) package.

### Entities

- **[`User`](src/backend/src/main/java/com/gotogether/backend/model/User.java)** (`users` table): `id : UUID`, `name`, `password`, `email` (unique), `socialBattery` (0–100, default 100), `currency` (default 0), `experiencePoints` (default 0), a many-to-many list of `interests` (linked to `Topic` via the `user_interests` join table), `lastLogin`, and an embedded `Settings`. New users are constructed with the three-argument constructor that sets all defaults.
- **[`Company`](src/backend/src/main/java/com/gotogether/backend/model/Company.java)** (`companies` table): `id : UUID`, `name`, `password`, `email` (unique), `currency` (default 0), and embedded `Address` and `Location`. Companies fund the currency rewards they offer for their challenges.
- **[`Challenge`](src/backend/src/main/java/com/gotogether/backend/model/Challenge.java)** (`challenges` table): `id : UUID`, `title`, `description`, `isArchived`, `startTime`, embedded `Location`, `durationMinutes`, `currency`, `experiencePoints`, `minSocialBattery`, a five-character `verificationCode`, `maxPlayers` (0 means unlimited), a many-to-many list of `topics` (`challenge_topics` join table), a mandatory `host` (`Company`), and the many-to-many list of participating `users` (`challenge_users` join table).
- **[`Topic`](src/backend/src/main/java/com/gotogether/backend/model/Topic.java)** (`topics` table): a simple `id : UUID` and unique `name`. Topics are referenced both as a user's interests and as the themes attached to a challenge.

### Embeddables

- **[`Address`](src/backend/src/main/java/com/gotogether/backend/model/Address.java):** `street`, `houseNumber`, `zipCode`, `city` – embedded into `Company`.
- **[`Location`](src/backend/src/main/java/com/gotogether/backend/model/Location.java):** `latitude` and `longitude` as `double` – embedded into `Company` and `Challenge`.
- **[`Settings`](src/backend/src/main/java/com/gotogether/backend/model/Settings.java):** embedded into `User` to keep per-user settings inline on the `users` table.

### DTOs

DTOs in the [dto](src/backend/src/main/java/com/gotogether/backend/dto) package decouple the API contract from the JPA entities. Request DTOs carry only the fields the client must supply; response DTOs deliberately omit sensitive data such as passwords and verification codes.

- **User & Company auth:** [`UserCreateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserCreateDTO.java) (`username`, `password`, `email`), [`UserLoginDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserLoginDTO.java), [`CompanyCreateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyCreateDTO.java) (account plus address and location), [`CompanyLoginDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyLoginDTO.java).
- **User & Company views:** [`UserDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserDTO.java) exposes `id`, `name`, `email`, `socialBattery`, `currency`, the derived `level` and `levelXp`, the list of `interests` (topic ids), `lastLogin`, and `settings`. [`CompanyDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyDTO.java) flattens the embedded `Address` and `Location` into top-level fields for easier consumption by the frontend.
- **Challenge views and operations:** [`ChallengeDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeDTO.java) (all challenge fields plus the derived `currentPlayers`, `hostCompanyName` and `topicIds`; `verificationCode` is intentionally omitted), [`ChallengeCreateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeCreateDTO.java) (company credentials plus the new challenge's payload; most fields are optional and fall back to service defaults), [`ChallengeCreatedDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeCreatedDTO.java) (returned after creation with the new id, the verification code and the QR code as a Base64-encoded PNG), [`ChallengeAuthenticateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeAuthenticateDTO.java) (company credentials for delete), [`ChallengeParticipanceDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeParticipanceDTO.java) (user credentials, current coordinates, challenge id and verification code), [`ChallengeVerificationDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeVerificationDTO.java) (id and verification code).
- **Challenge filter:** [`ChallengeFilterDTO`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeFilterDTO.java) bundles all optional filter, sort and paging parameters (substring filters, time and duration ranges, reward minima, social-battery affordability, player caps, geographic radius, host name, topic ids, two sort keys with directions of type [`ChallengeSortAttribute`](src/backend/src/main/java/com/gotogether/backend/dto/ChallengeSortAttribute.java), and a `limit`). Only `latitude` and `longitude` are mandatory because the result is always sorted by distance as the final tiebreaker.

### Mappers

Mappers in [mapper](src/backend/src/main/java/com/gotogether/backend/mapper) convert entities into DTOs.

- **[`UserMapper`](src/backend/src/main/java/com/gotogether/backend/mapper/UserMapper.java)** converts a `User` into a `UserDTO` and derives the gameplay-facing `level` and `levelXp` from `experiencePoints` using a geometric progression (base 100 XP, growth factor 1.15, capped at level 100). The password is never copied into the DTO.
- **[`CompanyMapper`](src/backend/src/main/java/com/gotogether/backend/mapper/CompanyMapper.java)** flattens the embedded `Address` and `Location` of a `Company` into a `CompanyDTO`.
- **[`ChallengeMapper`](src/backend/src/main/java/com/gotogether/backend/mapper/ChallengeMapper.java)** produces a `ChallengeDTO` (including the derived `currentPlayers`, `hostCompanyName` and `topicIds`, but excluding `verificationCode`) and a separate `ChallengeVerificationDTO` for trusted callers that need the verification code.

### Services

Business logic lives in [services](src/backend/src/main/java/com/gotogether/backend/services).

- **[`UserService`](src/backend/src/main/java/com/gotogether/backend/services/UserService.java)** creates users (enforcing email uniqueness across both `users` and `companies`, plus format and field validation), authenticates them on login (updating `lastLogin`), and lets a user update their `socialBattery` (0–100) or replace their `interests` with a deduplicated list of existing topic ids.
- **[`CompanyService`](src/backend/src/main/java/com/gotogether/backend/services/CompanyService.java)** mirrors the user flow for companies (signup, login, lookup, listing) and additionally lets a company top up its in-app currency via `addCompanyCurrency`. It validates the address fields and the latitude/longitude ranges before persisting.
- **[`TopicService`](src/backend/src/main/java/com/gotogether/backend/services/TopicService.java)** provides simple CRUD over topics (lookup, list, create with unique name, delete by id).
- **[`ChallengeService`](src/backend/src/main/java/com/gotogether/backend/services/ChallengeService.java)** is the most complex service and covers the full challenge lifecycle:
  - `getChallengeById` / `getChallengesByFilter` for read access. The filter pipeline applies every supplied criterion in memory, sorts by up to two explicit attributes with a distance tiebreaker, and enforces a hard cap of 100 results (default 10). The geographic radius uses the Haversine formula on the WGS-84 sphere.
  - `createChallenge` authenticates the host company, validates and defaults the inputs (duration → 120 min, currency reward → 100, location → company location, etc.), resolves the topic ids to entities, derives the XP reward via `calculateExperiencePoints`, generates a five-character verification code and the matching QR code (PNG, Base64-encoded), transfers the currency reward from the company to the challenge, and persists everything.
  - `deleteChallenge` authenticates the host, ensures the challenge is owned by it, refunds the challenge's currency to the company, and removes the challenge.
  - `participateInChallenge` authenticates the user, checks they are within ~400 m of the challenge, that the challenge still has capacity, that the user is not already enrolled, that the per-user cooldown (10 s) has elapsed, and that the verification code matches; on success the user is added to the participant list and credited the currency and experience point rewards.
  - `verifyChallenge` returns a `ChallengeVerificationDTO` when the supplied code matches the stored one.

### Controllers and Endpoints

All controllers live in [controller](src/backend/src/main/java/com/gotogether/backend/controller) and are mounted under `/api`.

- **[`UserController`](src/backend/src/main/java/com/gotogether/backend/controller/UserController.java)** (`/api/users`): `GET /{id}`, `GET /` (list all), `POST /signup`, `POST /login`, `PUT /preferences/socialBattery/{userId}` (body: new value), `PUT /preferences/interests/{userId}` (body: list of topic ids).
- **[`CompanyController`](src/backend/src/main/java/com/gotogether/backend/controller/CompanyController.java)** (`/api/companies`): `GET /{id}`, `GET /` (list all), `POST /signup`, `POST /login`, `PUT /currency/{companyId}` to top up the company's currency.
- **[`TopicController`](src/backend/src/main/java/com/gotogether/backend/controller/TopicController.java)** (`/api/topics`): `GET /{id}`, `GET /` (list all), `POST /` (body: topic name), `DELETE /{id}`.
- **[`ChallengeController`](src/backend/src/main/java/com/gotogether/backend/controller/ChallengeController.java)** (`/api/challenges`): `GET /{id}`, `POST /filter` (body: `ChallengeFilterDTO`), `POST /` (body: `ChallengeCreateDTO`, returns `201 Created` with a `ChallengeCreatedDTO`), `DELETE /{id}` (body: company credentials), `POST /participate` (body: `ChallengeParticipanceDTO`).

Authentication errors map to `401 UNAUTHORIZED`, lookup errors to `404 NOT FOUND`, validation and business-rule violations to `400 BAD REQUEST`, and unexpected errors to `500 INTERNAL SERVER ERROR`.

### Tests

The backend ships with JUnit tests under [src/backend/src/test/java/com/gotogether/backend](src/backend/src/test/java/com/gotogether/backend). Each service has a dedicated test class — [`UserServiceTest`](src/backend/src/test/java/com/gotogether/backend/services/UserServiceTest.java), [`CompanyServiceTest`](src/backend/src/test/java/com/gotogether/backend/services/CompanyServiceTest.java), [`TopicServiceTest`](src/backend/src/test/java/com/gotogether/backend/services/TopicServiceTest.java) and [`ChallengeServiceTest`](src/backend/src/test/java/com/gotogether/backend/services/ChallengeServiceTest.java) — covering the happy paths as well as the validation and authentication errors described above. [`BackendApplicationTests`](src/backend/src/test/java/com/gotogether/backend/BackendApplicationTests.java) is the standard Spring Boot context-load smoke test.

### Conventions

- All entity primary keys use `GenerationType.UUID`.
- Embeddables (`Address`, `Location`, `Settings`) keep related columns inline on the owning entity table.
- Emails are stored and compared in trimmed, lower-case form, and must be unique across both `users` and `companies`.
- DTOs never expose passwords or the challenge `verificationCode` (except in the dedicated `ChallengeVerificationDTO` / `ChallengeCreatedDTO`).
- Services throw `RuntimeException` with a human-readable message; controllers translate these into the appropriate HTTP status codes.

## DrawIO Architecture Diagramm

Outdated architecture diagramm from the proposal phase, but still gives a good overview of the main components and their interactions.

![Architecture Diagramm](docs/architecture/architecture.png)
<!-- TODO: Update obsolete architecture -->