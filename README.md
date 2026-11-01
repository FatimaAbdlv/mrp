Media Ratings Platform (Zwischenabgabe)

REST-Server in Java für eine Medienplattform. Benutzer registrieren sich, melden sich an und legen Filme, Serien oder Spiele an. Nur der Ersteller eines Mediums darf es ändern oder löschen.
Die Zwischenabgabe enthält Registrierung, Login mit Token und die Verwaltung von Medien. Bewertungen, Favoriten, Filter, Empfehlungen und das Leaderboard folgen in der Endabgabe.
GitHub: https://github.com/FatimaAbdlv/mrp

Technik:

Java (JDK 21 oder neuer), Maven
Der HttpServer aus dem JDK, ohne Framework
PostgreSQL 16 in Docker
JDBC mit PreparedStatement
Jackson für JSON

Starten:

Docker Desktop starten.
Im Projektordner die Datenbank starten mit dem Befehl: docker compose up -d
Beim ersten Start legt die Datei init.sql die Tabellen users und media_entries an.
Das Projekt in IntelliJ öffnen und die Klasse Main starten. Der Server läuft dann auf http://localhost:8080.
Datenbank-Zugang: Datenbank, Benutzer und Passwort heißen jeweils mrp (Port 5432).

Testen:

Bei laufendem Server und laufender Datenbank im Projektordner ausführen: bash test.sh
Das Skript schickt 30 curl-Anfragen (Erfolgs- und Fehlerfälle) und gibt zu jeder den Statuscode aus. Der erwartete Code steht jeweils in der Überschrift.
Endpunkte

Ohne Token:

POST /api/users/register: Benutzer registrieren
POST /api/users/login: Anmelden, liefert ein Token
GET /api/media: Alle Medien lesen
GET /api/media/{id}: Ein Medium lesen
Mit Token:
GET /api/me: Zeigt die eigene Benutzer-id
POST /api/media: Medium anlegen
PUT /api/media/{id}: Medium ändern (nur Ersteller)
DELETE /api/media/{id}: Medium löschen (nur Ersteller)
Das Token wird als Header mitgeschickt: Authorization: Bearer TOKEN
Struktur

http:
Router und Controller, nehmen Anfragen an und schicken Antworten
service: Regeln und Prüfungen, Benutzer und Medien
repository: Zugriff auf die Datenbank (hier steht das SQL)
model: User, MediaEntry, MediaType, Rating

Vereinfachungen:

Passwörter werden mit SHA-256 ohne Salt gespeichert.
Tokens liegen nur im Speicher und sind nach einem Neustart des Servers ungültig.
Genres werden als Text mit Komma getrennt in einer Spalte gespeichert.
