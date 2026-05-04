# Backend Guide

This folder contains the Spring Boot backend for the project.

The goal of this structure is to keep the code easy to understand, easy to test, and easy to grow.

## Main Parts

### `controller`
Controllers handle HTTP requests and responses.

Use them for:
- routes like `GET /api/v1/quests`
- request validation entry points
- passing data to the service layer

Keep controllers small. They should not contain business logic.

### `service`
Services contain business logic.

Use them for:
- rules and calculations
- coordination between multiple parts of the application
- deciding what should happen after a request arrives

If a controller starts getting complicated, move the logic into a service.

### `dto`
DTO means Data Transfer Object.

Use DTOs for:
- input from the client, such as request bodies
- output returned by the API

DTOs help keep your API stable and prevent exposing internal data structures directly.

### `entity` or `model`
Entities or models represent the data of the application.

Use them for:
- users, quests, rewards, and other domain objects
- persistence with a database later on

In this starter project, the code uses DTOs and an in-memory service. If you add a database, this is where entities usually go.

### `exception`
Exceptions represent application errors.

Use them for:
- not found errors
- invalid state errors
- custom API error handling

### `config`
Configuration classes hold shared setup.

Use them for:
- CORS setup
- security setup
- bean configuration
- API documentation setup

## Naming Convention

Try to keep naming simple and consistent:
- `SomethingController` for HTTP endpoints
- `SomethingService` for business logic
- `SomethingRequest` for incoming DTOs
- `SomethingResponse` for outgoing DTOs
- `SomethingEntity` for database models

## Recommended Flow

1. A controller receives the request.
2. The controller calls a service.
3. The service applies business logic.
4. The service returns a DTO or entity.
5. The controller sends the response.

## Example

If you want to add a new feature like users or check-ins:

- create a controller for the API route
- create a service for the logic
- create request and response DTOs
- add an entity later if the feature is stored in a database

## Current Starter Setup

The current starter backend includes:
- a health endpoint at `/api/v1/health`
- a sample quest controller at `/api/v1/quests`
- in-memory quest storage for now

This is intentionally small so the architecture can grow step by step.