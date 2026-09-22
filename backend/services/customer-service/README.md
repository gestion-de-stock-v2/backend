# customer-service

Gestion des clients.

| | |
|---|---|
| Port | `8090` |
| Stockage | MongoDB `customer` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/customer-service.yml` |

## Endpoints

- `/api/v1/customers` — CRUD

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up customer-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/customer-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
