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
    "id": "8f522749-1495-4d1e-8c99-bec15efff06e",
    "name": "Alice Johnson",
    "passwordHash": "$2a$10$hash1",
    "email": "alice@example.com",
    "socialBattery": 80,
    "currency": 500,
    "experiencePoints": 1200,
    "lastLogin": "2026-05-15T18:43:52.731445"
  }, ...
]
```

## Frontend (missing)

tbd