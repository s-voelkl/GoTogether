# Usage Guide

This usage guide provides instructions for setting up the GoTogether project, including backend and frontend configurations, database setup, and running the application.

Start with the general setup, then continue either with the database and backend setup or the frontend setup, depending on your needs.

## General Setup

Create a `.env` file in the `src` directory and add the following secrets (values are customizable):

```yaml
POSTGRES_USER: yourUsername
POSTGRES_PASSWORD: yourPassword
POSTGRES_DB: gotogether_db
```

> Do not commit the `.env` file to version control, to prevent leaking sensitive information!

## Database Setup

1. Open a terminal (e.g., in VS Code) and navigate to the `src` folder: `cd ./src`
2. All commands are defined in [src/package.json](src/package.json).
3. To start the database, run: `yarn db`

## Backend Setup

1. Configure secrets in the backend:

    [src/backend/src/main/resources/application.properties](src/backend/src/main/resources/application.properties):

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/gotogether_db
    spring.datasource.username=yourUsername
    spring.datasource.password=yourPassword
    ```

    [src/backend/src/test/resources/application.properties](src/backend/src/test/resources/application.properties):

    ```properties
    spring.datasource.url=jdbc:h2:mem:gotogether_db;
    spring.datasource.username=yourTestUsername
    spring.datasource.password=yourTestPassword
    ```

    > Do not commit the authentication credentials to version control, to prevent leaking sensitive information!

2. Download the Java JDK [AWS Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html).

3. Install Yarn version 1.x: [Yarn Installation Guide](https://classic.yarnpkg.com/en/docs/install).

4. Docker:

    - **For Windows Users**: Everything MUST be executed within WSL for Docker to function correctly. Ensure WSL has sufficient memory and RAM allocated ([WSL Installation Guide](https://learn.microsoft.com/en-us/windows/wsl/install)). Start WSL using the `wsl` command. `sudo` privileges may be required for certain Docker operations, such as running `nodemon` as described below.
    - **For Mac Users**: Install Docker using `brew install --cask docker`.

5. Install Nodemon for automatic backend restarts: `npm install -g nodemon`.

6. VS Code Extensions: Docker, Java Extension Pack, and Test Runner for Java (for unit tests).

7. Backend starting:

    - Start the backend server (REST API on port 8080) with: `yarn backend`
    - All REST API endpoints can be viewed via Swagger UI at: `http://localhost:8080/swagger-ui/index.html`
    - The user table comes pre-populated with 10 sample entries.

    Example Request: `curl http://localhost:8080/api/users`

    Example Response:

    ```json
    [
      {
        "id": "a9c632aa-4124-4760-9746-770a9b7fd30d",
        "name": "Alice Johnson",
        "password": "$2a$10$hash1",
        "email": "alice@example.com",
        "socialBattery": 80,
        "currency": 0,
        "experiencePoints": 0,
        "interests": [
          "cd00e5c8-c255-49c0-a6ab-4b3fe35e84bf",
          "43028bcf-8a21-4c3c-a779-2564c3b1c85b",
          "43aed8eb-61bf-4b2c-b64f-530cb617c33e"
        ],
        "lastLogin": "2026-05-27T21:09:16.326146",
        "settings": {
          "setting": "default"
        }
      }, ...
    ]
    ```

## Frontend Setup

1. Ensure Yarn version 1.x is installed: [Yarn Installation](https://classic.yarnpkg.com/en/docs/install).
2. TypeScript and Expo are already included in [src/frontend/package.json](src/frontend/package.json).
3. Navigate to the frontend directory: `cd ./src/frontend/`
4. Install dependencies: `yarn install`

### Frontend Android Setup

This should be run directly on Windows or macOS, **not** within WSL. It requires Android Studio and the Android SDK to run the Expo app on an Android device or emulator.

1. Follow the [React Native Environment Setup](https://reactnative.dev/docs/environment-setup) tutorial step by step.
2. Install the Expo Dev Client for the Android device: `npx install expo-dev-client`.
3. Verify the device connection: `adb devices`. If the device is not listed, ensure USB debugging is enabled and drivers are installed.
4. Run the app on the device: `npx expo run:android --device` and select your device.
5. Alternatively, open the web client with `yarn web` or `yarn expo start --dev-client`.
6. You might need to modify [src/frontend/android/settings.gradle](src/frontend/android/settings.gradle).

Refer to the [Expo Setup Guide](https://docs.expo.dev/build/setup/#install-the-latest-eas-cli) for additional help.
