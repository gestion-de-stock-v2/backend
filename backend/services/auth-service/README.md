# auth-service

Authentification JWT, gestion des utilisateurs et des 7 rôles.

| | |
|---|---|
| Port | `8085` |
| Stockage | PostgreSQL `auth` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/auth-service.yml` |

## Endpoints

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register` — **le rôle est imposé par le serveur** (`OBSERVATEUR`)
- `POST /api/v1/auth/forgot-password` — n'expose jamais le jeton ; le lien part par e-mail (Kafka)
- `POST /api/v1/auth/reset-password`, `/change-password`, `GET /me`
- `GET|DELETE /api/v1/users`, `PATCH /{id}/active`, `PUT /{id}/role` — **ADMIN**

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up auth-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/auth-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
