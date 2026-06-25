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

Follow this tutorial step by step: [https://reactnative.dev/docs/environment-setup](https://reactnative.dev/docs/environment-setup)

Install the Expo Dev Client for running on the Android device: `npx install expo-dev-client`
Verifiy that the Android device is connected and recognized by running `adb devices`. If the device is not listed, ensure that USB debugging is enabled on the device and that the necessary drivers are installed.

Run the Expo app on the Android device with `npx expo run:android --device` and select your device.
Alternatively, open the web client with `yarn web` or `yarn expo start --dev-client`.

This page can help out: [https://docs.expo.dev/build/setup/#install-the-latest-eas-cli](https://docs.expo.dev/build/setup/#install-the-latest-eas-cli)

You might need to change the ``src/frontend/android/settings.gradle`` file.

```gradle


pluginManagement {
def utf8 = java.nio.charset.StandardCharsets.UTF_8
  def reactNativeGradlePlugin = new File(
  new String(providers.exec {
      workingDir(rootDir)
      commandLine("node", "--print", "require.resolve('@react-native/gradle-plugin/package.json', { paths: [require.resolve('react-native/package.json')] })")
  }.standardOutput.asBytes.get(), utf8).trim()
  ).getParentFile().absolutePath
  includeBuild(reactNativeGradlePlugin)
  
  def expoPluginsPath = new File(
  new String(providers.exec {
      workingDir(rootDir)
      commandLine("node", "--print", "require.resolve('expo-modules-autolinking/package.json', { paths: [require.resolve('expo/package.json')] })")
  }.standardOutput.asBytes.get(), utf8).trim(),
    "../android/expo-gradle-plugin"
  ).absolutePath
  includeBuild(expoPluginsPath)
}
 
...
 
```
