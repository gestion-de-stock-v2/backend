# stock-service

Cœur du domaine : produits, catégories, fournisseurs et mouvements de stock.

| | |
|---|---|
| Port | `8050` |
| Stockage | PostgreSQL `stock` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/stock-service.yml` |

## Endpoints

- `/api/v1/products` — CRUD
- `POST /api/v1/products/purchase` — décrémente le stock (appelé par order-service)
- `POST /api/v1/products/restore` — compensation de saga
- `/api/v1/categories`, `/api/v1/suppliers` — CRUD
- `/api/v1/stock-movements` — liste, `product/{id}`, création (entrée/sortie)

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up stock-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/stock-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
