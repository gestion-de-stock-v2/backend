# stock-service

Port `8050` · PostgreSQL `stock`

Service central du domaine. Il fusionne le catalogue produit et la gestion de stock,
qui existaient auparavant en double dans deux services distincts.

## Produits — `/api/v1/products`

| Méthode | Chemin | Effet |
|---|---|---|
| `GET` | `/` | Liste |
| `GET` | `/{product-id}` | Détail |
| `POST` | `/` | Création → renvoie l'identifiant |
| `PUT` | `/{product-id}` | Mise à jour |
| `DELETE` | `/{product-id}` | Suppression |
| `POST` | `/purchase` | Décrément de stock (appelé par order-service) |
| `POST` | `/restore` | Compensation de saga |

Corps de création :

```json
{
  "name": "Dell Latitude 5440",
  "description": "Portable 14 pouces",
  "price": 685000.00,
  "availableQuantity": 25,
  "categoryId": 1,
  "supplierId": 1
}
```

### `POST /purchase`

```json
[ { "productId": 1, "quantity": 2 }, { "productId": 3, "quantity": 5 } ]
```

Les lignes sont **regroupées par produit** avant traitement : une même référence
présente deux fois voit ses quantités additionnées. Les produits sont chargés sous
verrou pessimiste et dans l'ordre de leurs identifiants, ce qui empêche à la fois
la survente et l'interblocage entre transactions concurrentes.

Stock insuffisant → `400` :
`Stock insuffisant pour le produit 3 (disponible : 2, demandé : 5)`

### `POST /restore`

Même corps que `/purchase`. Réincrémente le stock. *Best-effort* : un produit
supprimé entre-temps est journalisé en WARN sans faire échouer la compensation.

## Catégories — `/api/v1/categories`

CRUD complet. `name` obligatoire.

## Fournisseurs — `/api/v1/suppliers`

CRUD complet. Champs : `name` (obligatoire), `registrationNumber`, `phone`, `email`.

## Mouvements de stock — `/api/v1/stock-movements`

| Méthode | Chemin | Effet |
|---|---|---|
| `GET` | `/` | Tous les mouvements, plus récents d'abord |
| `GET` | `/product/{product-id}` | Mouvements d'un produit |
| `POST` | `/` | Enregistre un mouvement et ajuste le stock |

```json
{ "productId": 1, "type": "ENTRY", "quantity": 10, "note": "Réapprovisionnement" }
```

`type` vaut `ENTRY` (entrée) ou `EXIT` (sortie). Une sortie supérieure au stock
disponible est refusée en `400`. Le produit est chargé sous verrou pessimiste.
