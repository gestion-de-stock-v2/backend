# discovery

Registre Eureka. Tous les services s'y enregistrent ; la passerelle et les clients internes y résolvent les adresses.

| | |
|---|---|
| Port | `8761` |
| Stockage | — |
| Configuration | `backend/services/config-server/src/main/resources/configurations/discovery-service.yml` |

## Endpoints

- `GET /` — tableau de bord Eureka

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up discovery

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/discovery spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
