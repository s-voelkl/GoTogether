# GoTogether

Social networks connect people digitally, but rarely foster genuine relationships.
Users are looking for real-world encounters, lasting friendships, or at least deeper social interactions.

## LICENSE

The source code of this project is released under the MIT License. For more information, see the license file `src/LICENSE`.
The documentation and all accompanying materials are released under the CC-BY 4.0 License (see `documentation/LICENSE`).

## Repository Structure

- `src/backend`: Java Spring Boot backend with REST API
  - `main`: Main application class and configuration
    - `config`: Security configurations
    - `controller`: REST controllers for endpoints
    - `dto`: Data Transfer Objects for API communication
    - `mappter`: Mappers for converting between entities and DTOs
    - `model`: JPA entities and embeddables for the database
    - `repository`: JPA repositories for database access
    - `services`: Business logic and service layer
  - `test`: Unit and integration tests
    - `services`: Tests for service layer
- `src/frontend`: React Native app
  - `assets`: Images and icons
  - `src`: Main app logic and screens
    - `components`: Reusable UI components
    - `context`: React contexts for state management
    - `data`: Data management and state handling
    - `navigation`: Route definitions and navigation logic
    - `screens`: Main screens of the app
    - `theme`: Theming and styling

## Requirement Definitions

The underlying research and problem definition for the GoTogether project can be found in [research-problem-definition.md](docs/requirements/research-problem-definition.md).

From there, the personas in [personas.md](docs/requirements/personas.md) were derived, though especially the personas Karin (54) and Tom (29) highlight the main target groups for the app. The basic app idea is described in [app-idea.md](docs/requirements/app-idea.md). Based on this idea, the user stories and the reduced MVP user stories are defined in [user-stories-mvp.md](docs/requirements/user-stories-mvp.md). The SMART goal definition for the project can be found in [smart-goal.md](docs/requirements/smart-goal.md).

## Architecture Description

The [architecture description](docs/architecture/architecture.md) describes the overall architecture of the GoTogether app, including the main components, interfaces, and technologies used. It also includes a high-level overview of the system design and the rationale behind architectural decisions.
