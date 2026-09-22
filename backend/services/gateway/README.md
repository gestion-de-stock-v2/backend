# gateway

Point d'entrée unique du système. Valide le JWT de chaque requête, applique la politique CORS et route vers les services via Eureka.

| | |
|---|---|
| Port | `8222` |
| Stockage | — |
| Configuration | `backend/services/config-server/src/main/resources/configurations/gateway-service.yml` |

## Endpoints

- Routes déclarées explicitement ; `discovery.locator` est **désactivé** (il exposait le config-server).
- Chemins publics : `/api/v1/auth/{login,register,forgot-password,reset-password}`
- Tout le reste exige `Authorization: Bearer <jeton>`
- Propage `X-User-Name` et `X-User-Role` en aval, après avoir effacé ceux fournis par le client.

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up gateway

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/gateway spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
