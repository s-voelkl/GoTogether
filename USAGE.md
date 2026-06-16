# Startanleitung

## Voraussetzungen

1. .env-Datei im Ordner `src` anlegen und folgende Secrets hinzufügen (secret value ist frei wählbar)

    ```yaml
    POSTGRES_USER: meinBenutzername
    POSTGRES_PASSWORD: meinPasswort
    POSTGRES_DB: gotogether_db
    ```

2. Secrets im Backend anpassen:

    `src/backend/src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/gotogether_db
    spring.datasource.username=myUsername
    spring.datasource.password=myPassword
    ```

    `src/backend/src/test/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:h2:mem:gotogether_db;
    spring.datasource.username=myTestUsername
    spring.datasource.password=myTestPassword
    ```

3. Java JDK [AWS Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) downloaden.

4. yarn version 1.x installieren: [Yarn Installation](https://classic.yarnpkg.com/en/docs/install).

5. Docker:
    Für **Windows User**: Alles MUSS in wsl stattfinden, damit docker verwendet werden kann.
    Achtet darauf wsl genügend Speicher und RAM zuzuweisen ([WSL Installer](https://learn.microsoft.com/de-de/windows/wsl/install)).
    WSL starten mit `wsl`.
    Teils braucht man `sudo` Rechte, um docker zu verwenden, z.B. bei ``nodemon`` unten.

    Für **Mac User**: Docker installieren mit (``brew install --cask docker``)

6. Nodemon für automatischen Neustart des Backends installieren: `npm install -g nodemon`

7. VS Code Extensions: Docker, Java Extension Pack, Test Runner for Java (für Unit Tests)

## Terminal

Starte ein Terminal (z.B. in VSCode) und navigiere zu `GoTogether/src`: `cd ./src`

## Datenbank

Alle commands sind zentral in `src/package.json` zu finden.

Für die Datenbank muss `yarn db` ausgeführt werden.

## Backend

Mit `yarn backend` wird das backend (REST API auf Port 8080) gestartet.

Mit Hilfe der swagger-ui kann man alle REST-API Endpoints anzeigen lassen: `http://localhost:8080/swagger-ui/index.html/`

Es befinden sich 10 sample entries im users table.

Request: `curl http://localhost:8080/api/users`

Response:

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

## Frontend

- Install yarn version 1.x: [Yarn Installation](https://classic.yarnpkg.com/en/docs/install).
- Typescript and Expo are already included in `src/frontend/package.json`.
- Navigate to ``cd ./src/frontend/``
- Install dependencies with `yarn install`

### Android Setup

This should not be run with WSL, but directly on Windows or MacOS. It requires the installation of Android Studio and the Android SDK, which are necessary for running the Expo app on an Android device or emulator.

- Android Studio: Download and install Android Studio.
- Android SDK: Open Android Studio, go to the ``Tools > SDK Manager`` and ensure you have installed:
  - Android SDK Platform (usually the latest, e.g., API 34 or 35)
  - Android SDK Build-Tools
  - Android SDK Command-line Tools (normally not automatically installed)
- Environment Variables: Add the Android SDK paths to your shell configuration file (e.g., ~/.bashrc or ~/.zshrc):

    ```bash
    export ANDROID_HOME=$HOME/Android/Sdk
    export PATH=$PATH:$ANDROID_HOME/emulator
    export PATH=$PATH:$ANDROID_HOME/platform-tools
    ```

Linux:

- Edit e.g. with `nano ~/.bashrc` and add the above lines at the end of the file.
- Run `source ~/.bashrc` afterwards.

Windows:
Go to System Properties > Environment Variables and add the following variables:

- Variable name `ANDROID_HOME`, value `C:\Users\YourUsername\AppData\Local\Android\Sdk` (adjustment needed)
- Edit the "Path": New `%ANDROID_HOME%\emulator` and `%ANDROID_HOME%\platform-tools`

Be sure to have Android development options enabled on your Android device and connect it via USB and allow USB debugging. Check connection via `adb devices` in the Windows Powershell. Emulation is possible via Android Studio.

Run via `yarn android` in `cd ./src/frontend`. On the first startup, this will install the Gradle dependencies. If errors occur, a system restart can help.

Known Issues:

- ``Class org.gradle.jvm.toolchain.JvmVendorSpec does not have member field '... IBM_SEMERU'``:
  Downgrading the Gradle version to a stable, supported release for React Native (8.13) in your ``gradle-wrapper.properties`` is possible. Then run `yarn android` again.
