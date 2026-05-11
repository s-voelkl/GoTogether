# Startanleitung

## Voraussetzungen

1. .env-Datei im root-folder anlegen und folgende Secrets hinzufügen (secret value ist frei wählbar)

    ```yaml
    POSTGRES_USER: 
    POSTGRES_PASSWORD: 
    POSTGRES_DB: 
    ```

2. Java JDK [AWS Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html) downloaden

3. yarn version 1.x installieren: [Yarn Installation](https://classic.yarnpkg.com/en/docs/install)

4. Docker:
    Für **Windows User**: Alles MUSS in wsl stattfinden, damit docker verwendet werden kann. Achtet darauf wsl genügend Speicher und RAM zuzuweisen ([WSL Installer](https://learn.microsoft.com/de-de/windows/wsl/install)). WSL starten mit `wsl`.

    Für **Mac User**: Docker installieren mit (``brew install --cask docker``)

## Terminal

Starte ein Terminal (z.B. in VSCode) und navigiere zu `GoTogether/src`: `cd ./src`

## Datenbank

Alle commands sind zentral in `src/package.json` zu finden.

Für die Datenbank muss `yarn db` ausgeführt werden.

## Backend

Mit `yarn backend` wird das backend (REST API auf Port 8080) gestartet.

Mit Hilfe der swagger-ui kann man alle REST-API Endpoints anzeigen lassen: `http://localhost:8080/swagger-ui/index.html/`


Es befinden sich 10 sample entries im users table.

Request: `curl http://localhost:8080/api/users/d0ef805b-ff1c-4588-95b4-aa98cd8fe17ecdc`

Response: `{"id":"d0ef805b-ff1c-4588-95b4-aa98cd8fe17e",
    "name":"Alice Johnson",
    "passwordHash":"$2a$10$hash1",
    "email":"alice@example.com",
    "socialBattery":80,
    "currency":500,
    "experiencePoints":1200,
    "lastLogin":"2026-05-09T09:13:15.481046"}%`

## Frontend (missing)

tbd