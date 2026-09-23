# config-server

Sert la configuration de tous les services depuis `src/main/resources/configurations/`, en profil `native`.

| | |
|---|---|
| Port | `8888` |
| Stockage | — |
| Configuration | `backend/services/config-server/src/main/resources/configurations/config-server-service.yml` |

## Endpoints

- `GET /{application}/{profile}` — configuration d'un service
- **Non exposé sur la machine hôte** : il sert les identifiants de toutes les bases.

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up config-server

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/config-server spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
