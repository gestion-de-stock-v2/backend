# payment-service

Enregistrement des paiements.

| | |
|---|---|
| Port | `8060` |
| Stockage | PostgreSQL `payments` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/payment-service.yml` |

## Endpoints

- `POST /api/v1/payments`
- Publie `payment-topic` sur Kafka.

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up payment-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/payment-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
