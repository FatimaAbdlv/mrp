#!/bin/bash

BASE="http://localhost:8080"
STAMP=$(date +%s)
USER1="benutzer1_$STAMP"
USER2="benutzer2_$STAMP"
PASSWORT="geheim"
JSON="Content-Type: application/json"

echo "=== 1. Benutzer 1 registrieren (erwartet 201) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/register" -H "$JSON" -d "{\"username\":\"$USER1\",\"password\":\"$PASSWORT\"}"

echo "=== 2. Benutzer 2 registrieren (erwartet 201) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/register" -H "$JSON" -d "{\"username\":\"$USER2\",\"password\":\"$PASSWORT\"}"

echo "=== 3. Benutzer 1 nochmal registrieren (erwartet 409) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/register" -H "$JSON" -d "{\"username\":\"$USER1\",\"password\":\"$PASSWORT\"}"

echo "=== 4. Registrieren ohne Passwort (erwartet 400) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/register" -H "$JSON" -d "{\"username\":\"ohnepasswort_$STAMP\"}"

echo "=== 5. Registrieren mit kaputtem JSON (erwartet 400) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/register" -H "$JSON" -d "kaputt"

echo "=== 6. Login mit falschem Passwort (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/login" -H "$JSON" -d "{\"username\":\"$USER1\",\"password\":\"falsch\"}"

echo "=== 7. Login mit unbekanntem Benutzer (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/login" -H "$JSON" -d "{\"username\":\"gibtsnicht_$STAMP\",\"password\":\"egal\"}"

echo "=== 8. SQL-Injection-Versuch beim Login (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/login" -H "$JSON" -d "{\"username\":\"' OR '1'='1\",\"password\":\"egal_$STAMP\"}"

echo "=== 9. Login Benutzer 1 (erwartet 200 und Token) ==="
RESPONSE=$(curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/login" -H "$JSON" -d "{\"username\":\"$USER1\",\"password\":\"$PASSWORT\"}")
echo "$RESPONSE"
TOKEN1=$(echo "$RESPONSE" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

echo "=== 10. Login Benutzer 2 (erwartet 200 und Token) ==="
RESPONSE=$(curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/users/login" -H "$JSON" -d "{\"username\":\"$USER2\",\"password\":\"$PASSWORT\"}")
echo "$RESPONSE"
TOKEN2=$(echo "$RESPONSE" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

echo "=== 11. Geschützter Endpunkt ohne Token (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/me"

echo "=== 12. Geschützter Endpunkt mit falschem Token (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/me" -H "Authorization: Bearer abc"

echo "=== 13. Geschützter Endpunkt mit Token (erwartet 200) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/me" -H "Authorization: Bearer $TOKEN1"

echo "=== 14. Medium anlegen ohne Token (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media" -H "$JSON" -d '{"title":"Inception","mediaType":"MOVIE","releaseYear":2010,"ageRestriction":12}'

echo "=== 15. Medium anlegen als Benutzer 1 (erwartet 201) ==="
RESPONSE=$(curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media" -H "$JSON" -H "Authorization: Bearer $TOKEN1" -d '{"title":"Inception","description":"Ein Traum im Traum","mediaType":"MOVIE","releaseYear":2010,"genres":["Action","Sci-Fi"],"ageRestriction":12}')
echo "$RESPONSE"
MEDIA_ID=$(echo "$RESPONSE" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')

echo "=== 16. Medium ohne Titel anlegen (erwartet 400) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media" -H "$JSON" -H "Authorization: Bearer $TOKEN1" -d '{"mediaType":"MOVIE","releaseYear":2010,"ageRestriction":12}'

echo "=== 17. Medium mit ungültigem Medientyp anlegen (erwartet 400) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media" -H "$JSON" -H "Authorization: Bearer $TOKEN1" -d '{"title":"Test","mediaType":"FILM","releaseYear":2010,"ageRestriction":12}'

echo "=== 18. Alle Medien lesen (erwartet 200) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media"

echo "=== 19. Ein Medium lesen (erwartet 200) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media/$MEDIA_ID"

echo "=== 20. Unbekanntes Medium lesen (erwartet 404) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media/999999"

echo "=== 21. Medium mit ungültiger id lesen (erwartet 400) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media/abc"

echo "=== 22. Medium ändern ohne Token (erwartet 401) ==="
curl -s -w "\nStatus: %{http_code}\n" -X PUT "$BASE/api/media/$MEDIA_ID" -H "$JSON" -d '{"title":"Inception Neu","mediaType":"MOVIE","releaseYear":2010,"ageRestriction":12}'

echo "=== 23. Medium ändern als Benutzer 2, nicht Ersteller (erwartet 403) ==="
curl -s -w "\nStatus: %{http_code}\n" -X PUT "$BASE/api/media/$MEDIA_ID" -H "$JSON" -H "Authorization: Bearer $TOKEN2" -d '{"title":"Inception Neu","mediaType":"MOVIE","releaseYear":2010,"ageRestriction":12}'

echo "=== 24. Medium ändern als Benutzer 1, Ersteller (erwartet 200) ==="
curl -s -w "\nStatus: %{http_code}\n" -X PUT "$BASE/api/media/$MEDIA_ID" -H "$JSON" -H "Authorization: Bearer $TOKEN1" -d '{"title":"Inception Neu","description":"Neue Beschreibung","mediaType":"MOVIE","releaseYear":2010,"genres":["Action","Sci-Fi"],"ageRestriction":12}'

echo "=== 25. Unbekanntes Medium ändern (erwartet 404) ==="
curl -s -w "\nStatus: %{http_code}\n" -X PUT "$BASE/api/media/999999" -H "$JSON" -H "Authorization: Bearer $TOKEN1" -d '{"title":"Egal","mediaType":"MOVIE","releaseYear":2010,"ageRestriction":12}'

echo "=== 26. Medium löschen als Benutzer 2, nicht Ersteller (erwartet 403) ==="
curl -s -w "\nStatus: %{http_code}\n" -X DELETE "$BASE/api/media/$MEDIA_ID" -H "Authorization: Bearer $TOKEN2"

echo "=== 27. Medium löschen als Benutzer 1, Ersteller (erwartet 204) ==="
curl -s -w "\nStatus: %{http_code}\n" -X DELETE "$BASE/api/media/$MEDIA_ID" -H "Authorization: Bearer $TOKEN1"

echo "=== 28. Gelöschtes Medium lesen (erwartet 404) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/api/media/$MEDIA_ID"

echo "=== 29. Unbekannter Pfad (erwartet 404) ==="
curl -s -w "\nStatus: %{http_code}\n" "$BASE/gibtsnicht"

echo "=== 30. Falsche Methode (erwartet 405) ==="
curl -s -w "\nStatus: %{http_code}\n" -X DELETE "$BASE/ping"