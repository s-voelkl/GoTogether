# Startanleitung

## Terminal

Starte ein Terminal (z.B. in VSCode) und navigiere zu `Isolaticy/src`

Für Windows User: Alles MUSS in wsl stattfinden, damit docker verwendet werden kann. Achtet darauf wsl genügend Speicher und RAM zuzuweisen.

## Datenbank

Alle commands sind zentral in src/package.json zu finden

Für die Datenbank muss `yarn db` ausgeführt werden

## Backend

Mit `yarn backend` wird das backend (REST API auf Port 8080) gestartet

Es befinden sich 10 sample entries im users table

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