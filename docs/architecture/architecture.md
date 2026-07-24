# GoTogether Architecture

## Three-Tier Architecture

The architecture follows the MVP and focuses on the core use cases from the sections MVP, Content User Stories, Technical User Stories, and Business-Related User Stories.

<div style="text-align:center">
  <img src="/docs/diagrams/out/three-tier-architecture/three-tier-architecture.png" alt="Three-Tier Architecture" width="250"/>
</div>

- **Presentation Tier:** The React Native app displays the open challenge list, the real-time map, social battery, matching, and reward status. It encapsulates only the UI and communicates with the backend via the API (see [Frontend-Architecture](#frontend-architecture)). A migration to a native Android app is currently in progress but not finished, so the React Native app is still the main presentation tier.
- **Application Tier:** The Java Spring Boot backend with a REST API interface implements the MVP logic: challenge filters, map queries, social battery filtering, interest matching, QR check-in, rewards, and company APIs for events or challenges (see [Backend-Architecture](#backend-architecture)).
- **Data Tier:** PostgreSQL stores the required data, including users, profiles, interests, quests, events, check-ins, friendships, rewards, and company profiles (see [Database-Architecture](#database-architecture)).

At the beginning, the architecture diagrams were made with DrawIO (diagrams.net), though the agile nature of the project needed a more organic approach to the architecture designing. We decided on using PlantUML to create the architecture diagrams, so only text blocks have to be edited instead of a drag-and-drop interface. The diagrams are automatically exported to PNG files and included in the documentation.

## Database-Architecture

The project uses a relational database model implemented with **PostgreSQL** in production and **H2** (in-memory) for testing. The schema is designed to support a multi-player challenge system with companies as hosts and users as participants.

<div style="text-align:center">
  <img src="/docs/diagrams/out/database-architecture/database-architecture.png" alt="Database Architecture" width="400"/>
</div>

### Key Characteristics

- **Primary Keys:** All tables use globally unique identifiers (**UUID v4**) as primary keys, ensuring scalability and preventing identifier collisions.
- **Embedded Patterns:** Frequently used data structures like `Address`, `Location` (latitude/longitude), and `Settings` are implemented as **JPA Embeddables**. This flattens these objects into the parent table's columns (e.g., `companies.street`, `challenges.latitude`), reducing join complexity for core entities.
- **Geospatial Data:** Geographic coordinates are stored as `double` precision values in embedded `Location` objects, enabling distance-based filtering and spatial queries at the application level.

### Relationship Model

The database manages complex interactions through the following relationships:

- **Many-to-Many Relationships:**
  - `user_interests`: Links `users` and `topics` to track individual interests.
  - `challenge_topics`: Links `challenges` and `topics` to categorize events.
  - `challenge_users`: Tracks participants for each `challenge`.
- **Many-to-One Relations:**
  - `challenges` -> `companies`: Every challenge is hosted by exactly one company.

### Table Schema Summary

- **[`topics`](src/backend/src/main/resources/data.sql#L8):** Reference table for categories (e.g., 'Sport', 'Natur') with associated UI metadata like icons and hex-string background colors.
- **[`users`](src/backend/src/main/resources/data.sql#L33):** Stores profile data (`name`, `email`, `social_battery`), performance stats (`currency`, `experience_points`), and embedded `settings`.
- **[`companies`](src/backend/src/main/resources/data.sql#L74):** Stores corporate profiles, authentication details, and embedded `address` and `location`.
- **[`challenges`](src/backend/src/main/resources/data.sql#L112):** Central entity linking hosts, participants, and rewards, including metadata like `start_time`, `duration_minutes`, and `min_social_battery`.

## Backend-Architecture

The backend is a Spring Boot application split into the standard layers: controllers expose REST endpoints, services contain the business logic, mappers translate between entities and DTOs, and JPA entities persist to PostgreSQL. All identifiers are `UUID`s generated with `GenerationType.UUID`, and controllers consistently return `ResponseEntity<?>` while mapping `RuntimeException`s to HTTP status codes. Security is configured to permit all requests by default, with `BCrypt` used for password hashing.

<div style="text-align:center">
  <img src="/docs/diagrams/out/backend-architecture/backend-architecture.png" alt="Backend Architecture"/>
</div>

### Security and Configuration

The [config](src/backend/src/main/java/com/gotogether/backend/config) package contains `SecurityConfig`, which disables CSRF and allows all HTTP requests to facilitate rapid development and testing. The `SecurityService` centralizes password hashing and verification using a `BCryptPasswordEncoder`.

### Database and Models

JPA entities and embeddables live in the [model](src/backend/src/main/java/com/gotogether/backend/model) package. The [repository](src/backend/src/main/java/com/gotogether/backend/repository) layer uses Spring Data JPA interfaces for database access.

### Entities

- **[`User`](src/backend/src/main/java/com/gotogether/backend/model/User.java)** (`users` table): `id : UUID`, `name`, `password`, `email` (unique), `socialBattery` (0–100, default 100), `currency` (default 0), `experiencePoints` (default 0), a many-to-many list of `interests` (linked to `Topic` via the `user_interests` join table), `lastLogin`, and an embedded `Settings`. New users are constructed with the three-argument constructor that sets all defaults.
- **[`Company`](src/backend/src/main/java/com/gotogether/backend/model/Company.java)** (`companies` table): `id : UUID`, `name`, `password`, `email` (unique), `currency` (default 0), and embedded `Address` and `Location`. Companies fund the currency rewards they offer for their challenges.
- **[`Challenge`](src/backend/src/main/java/com/gotogether/backend/model/Challenge.java)** (`challenges` table): `id : UUID`, `title`, `description`, `isArchived`, `startTime`, embedded `Location`, `durationMinutes`, `currency`, `experiencePoints`, `minSocialBattery`, a five-character `verificationCode`, `maxPlayers` (0 means unlimited), a many-to-many list of `topics` (`challenge_topics` join table), a mandatory `host` (`Company`), and the many-to-many list of participating `users` (`challenge_users` join table).
- **[`Topic`](src/backend/src/main/java/com/gotogether/backend/model/Topic.java)** (`topics` table): `id : UUID`, unique `name`, and optional `icon` and `backgroundColor` (hex-string). Topics are referenced both as a user's interests and as the themes attached to a challenge.

### Embeddables

- **[`Address`](src/backend/src/main/java/com/gotogether/backend/model/Address.java):** `street`, `houseNumber`, `zipCode`, `city` – embedded into `Company`.
- **[`Location`](src/backend/src/main/java/com/gotogether/backend/model/Location.java):** `latitude` and `longitude` as `double` – embedded into `Company` and `Challenge`.
- **[`Settings`](src/backend/src/main/java/com/gotogether/backend/model/Settings.java):** embedded into `User` to keep per-user settings inline on the `users` table.

### DTOs

DTOs in the [dto](src/backend/src/main/java/com/gotogether/backend/dto) package decouple the API contract from the JPA entities. Request DTOs carry only the fields the client must supply; response DTOs deliberately omit sensitive data such as passwords and verification codes.

- **User & Company auth:** [`UserCreateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserCreateDTO.java) (`username`, `password`, `email`), [`UserLoginDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserLoginDTO.java), [`CompanyCreateDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyCreateDTO.java) (account plus address and location), [`CompanyLoginDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyLoginDTO.java).
- **User & Company views:** [`UserDTO`](src/backend/src/main/java/com/gotogether/backend/dto/UserDTO.java) exposes `id`, `name`, `email`, `socialBattery`, `currency`, the derived `level` and `levelXp`, the list of `interests` (topic ids), `lastLogin`, and `settings`. [`CompanyDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CompanyDTO.java) flattens the embedded `Address` and `Location` into top-level fields for easier consumption by the frontend.
- **Topic creation:** [`CreateTopicDTO`](src/backend/src/main/java/com/gotogether/backend/dto/CreateTopicDTO.java) used for creating new topics with `name`, `icon`, and `backgroundColor`.
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
- **[`TopicService`](src/backend/src/main/java/com/gotogether/backend/services/TopicService.java)** provides CRUD over topics, including validation for background colors and ensuring unique names.
- **[`SecurityService`](src/backend/src/main/java/com/gotogether/backend/services/SecurityService.java)** provides utility methods for hashing and matching passwords using the configured encoder.
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
- **[`TopicController`](src/backend/src/main/java/com/gotogether/backend/controller/TopicController.java)** (`/api/topics`): `GET /{id}`, `GET /` (list all), `POST /` (body: `CreateTopicDTO`), `DELETE /{id}`.
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
- Security uses `BCrypt` for hashing and defaults to permitting all requests for testing purposes.

## Frontend-Architecture

The GoTogether frontend is a **Cross-Platform React Native** application built on the **Expo** framework. It follows a modular, component-driven architecture designed to provide a highly interactive, real-time social experience.

<div style="text-align:center">
  <img src="/docs/diagrams/out/frontend-architecture/frontend-architecture.png" alt="Frontend Architecture"/>
</div>

### Neo Brutalist Design System

The UI adheres to a **Neo Brutalist** aesthetic, characterized by:

- **High Contrast:** A vibrant palette anchored by `colors.primary` and deep black/white tones.
- **Raw Geometry:** Bold borders (`layout.border`), sharp shadows (`shadow.md`), and distinctive `continuous` border curves for a modern, tactile feel.
- **Expressive Typography:** The `Unbounded` font family is used across all layers to emphasize the playful and energetic nature of the app.

### Project Structure

The frontend logic is organized into specialized modules:

- **`screens/`**: Primary application views (e.g., [`HomeScreen`](src/frontend/src/screens/HomeScreen.tsx), [`ChallengesScreen`](src/frontend/src/screens/ChallengesScreen.tsx)) that orchestrate complex UI interactions.
- **`components/`**: Reusable UI atoms and molecules (e.g., [`AppHeader`](src/frontend/src/components/AppHeader.tsx), [`ChallengeCard`](src/frontend/src/components/ChallengeCard.tsx)) emphasizing high reusability and consistent styling.
- **`context/`**: Global state management leveraging the React Context API for reactive features like [`LocationContext`](src/frontend/src/context/LocationContext.tsx) and [`FiltersContext`](src/frontend/src/context/FiltersContext.tsx).
- **`navigation/`**: A strictly typed navigation system powered by **React Navigation**, defining the flow between the Map, Challenges, and Profile via a custom [`TabBar`](src/frontend/src/components/TabBar.tsx).
- **`theme/`**: The single source of truth for the design system, centralizing constants for colors, spacing, and shadows to ensure visual harmony.

### Key Integrations

- **MapLibre SDK:** Handles the real-time geographic visualization of challenges and user positions.
- **Reanimated & Gesture Handler:** Provides smooth, physics-based interactions for bottom sheets and overlays.

### Problems with React Native

- **Incompatibility with Android Devices:** The app often works on iOS but faces issues on various Android devices.
- **Setup Failures:** Team members cannot setup or start the Expo project consistently due to dependency and configuration issues.
- **Configuration Complexity:** The React Native environment requires frequent updates and adjustments, leading to a fragile development setup.

This led to the decision to migrate to a native Android application using **Kotlin** in **Android Studio**, leveraging the existing Java backend. This transition aims to provide a more stable development environment and better performance on Android devices. Existing React Native components will be gradually replaced with native Android components, ensuring a smooth transition while maintaining the core functionality of the app. The Android Studio project is located in the `src/android-frontend` directory, and the migration is ongoing.
