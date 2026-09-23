# order-service

Commandes et lignes de commande. Orchestre la saga de création : client → stock → paiement → notification.

| | |
|---|---|
| Port | `8070` |
| Stockage | PostgreSQL `orders` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/order-service.yml` |

## Endpoints

- `POST /api/v1/orders` — création (saga)
- `GET /api/v1/orders`, `GET /api/v1/orders/{id}`
- `/api/v1/order-lines`
- Publie `order-topic` sur Kafka après confirmation du paiement.

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up order-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/order-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
