# Gestion de stock — plateforme microservices

Backend de la plateforme de gestion de stock : catalogue produits, catégories,
fournisseurs, mouvements, clients, commandes, paiements et notifications par
e-mail. Architecture Spring Cloud, authentification JWT avec 7 rôles.

L'interface Angular vit dans un dépôt distinct :
[`gestion-de-stock-v2/frontend-angular`](https://github.com/gestion-de-stock-v2/frontend-angular).

---

## Démarrage en une commande

```bash
cp .env.example .env          # puis générez un JWT_SECRET : openssl rand -base64 48
docker compose up --build
```

Au premier lancement, comptez quelques minutes : les neuf services sont compilés
puis démarrés dans l'ordre imposé par leurs `healthcheck`.

### Avec l'interface

Le frontend étant dans un dépôt séparé, clonez-le **à côté** de celui-ci :

```
un-dossier/
├── backend/            ← ce dépôt
└── frontend-angular/   ← https://github.com/gestion-de-stock-v2/frontend-angular
```

```bash
docker compose --profile frontend up --build    # ajoute l'interface sur :4200
```

Sans ce profil, la pile backend démarre seule : elle n'a besoin de rien d'autre.

| Accès | URL |
|---|---|
| **Application** (profil `frontend`) | http://localhost:4200 |
| Passerelle API | http://localhost:8222 |
| Registre Eureka | http://localhost:8761 |
| Traces Zipkin | http://localhost:9411 |
| Boîte mail de test (MailDev) | http://localhost:1080 |

Outils d'administration des bases (optionnels) :

```bash
docker compose --profile tools up -d    # pgAdmin :5050, mongo-express :8081
```

Arrêt : `docker compose down` — ou `docker compose down -v` pour effacer aussi les données.

---

## Architecture

```
                        ┌──────────────────┐
  navigateur  ────────► │  frontend :4200  │  (nginx, relaie /api vers la passerelle)
                        └────────┬─────────┘
                                 ▼
                        ┌──────────────────┐
                        │  gateway :8222   │  seul point d'entrée, valide le JWT
                        └────────┬─────────┘
                                 │  (résolution par Eureka)
      ┌──────────────┬───────────┼───────────┬──────────────┬──────────────┐
      ▼              ▼           ▼           ▼              ▼              ▼
 auth :8085    stock :8050  customer :8090  order :8070  payment :8060  notification :8040
      │              │           │           │              │              │
   Postgres      Postgres     MongoDB     Postgres       Postgres       MongoDB
    (auth)        (stock)    (customer)    (orders)      (payments)   (notification)
                                               └──────► Kafka ◄────────────┘

  Socle : config-server :8888 (configuration centralisée) · discovery :8761 (Eureka)
  Observabilité : Zipkin :9411 · E-mails : MailDev :1080
```

Chaque service possède sa propre base (*Database per Service*). Les échanges
synchrones passent par OpenFeign et un `RestTemplate` équilibré par Eureka ; les
échanges asynchrones (confirmations de commande, de paiement, liens de
réinitialisation) passent par Kafka.

### Services

| Service | Port | Rôle | Stockage |
|---|---|---|---|
| `config-server` | 8888 | Configuration centralisée de tous les services | — |
| `discovery` | 8761 | Registre Eureka | — |
| `gateway` | 8222 | Point d'entrée unique, validation JWT, CORS | — |
| `auth-service` | 8085 | Authentification, utilisateurs, rôles | PostgreSQL `auth` |
| `stock-service` | 8050 | Produits, catégories, fournisseurs, mouvements | PostgreSQL `stock` |
| `customer-service` | 8090 | Clients | MongoDB `customer` |
| `order-service` | 8070 | Commandes et lignes de commande | PostgreSQL `orders` |
| `payment-service` | 8060 | Paiements | PostgreSQL `payments` |
| `notification-service` | 8040 | Envoi des e-mails | MongoDB `notification` |

---

## Sécurité

**Le gateway est le seul point d'entrée.** Il valide la signature et l'expiration
de chaque jeton, puis propage l'identité en aval via `X-User-Name` et `X-User-Role`.
Ces en-têtes sont systématiquement effacés de la requête entrante avant d'être
réécrits : un client ne peut pas se déclarer administrateur en les fournissant lui-même.

Seuls ces chemins sont accessibles sans jeton :
`/api/v1/auth/login`, `/register`, `/forgot-password`, `/reset-password`.

### Rôles

`ADMIN` · `GERANT` · `MAGASINIER` · `VENDEUR` · `ACHETEUR` · `COMPTABLE` · `OBSERVATEUR`

**L'inscription libre attribue toujours `OBSERVATEUR`** (lecture seule). Le champ
`role` n'existe pas dans la requête d'inscription : l'élévation de privilèges passe
exclusivement par `PUT /api/v1/users/{id}/role`, réservé aux `ADMIN`.

### Variables d'environnement sensibles

| Variable | Obligatoire | Remarque |
|---|---|---|
| `JWT_SECRET` | **oui** | 32 caractères minimum. `auth-service` et `gateway` refusent de démarrer sans. Doit être identique pour les deux. |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` | oui | — |
| `MONGO_USERNAME` / `MONGO_PASSWORD` | oui | — |
| `ADMIN_INITIAL_PASSWORD` | non | Si vide, un mot de passe aléatoire est généré et affiché **une seule fois** dans les logs d'`auth-service`. |

Aucun secret n'est versionné. `.env` est ignoré par git.

### Comptes de démonstration

Ces comptes n'existent **qu'avec `SPRING_PROFILES_ACTIVE=dev`** (valeur par défaut
de `.env.example`). Avec tout autre profil, seul le compte administrateur est créé,
avec un mot de passe non devinable.

| Identifiant | Mot de passe | Rôle |
|---|---|---|
| `admin` | `admin123!` | ADMIN |
| `gerant` | `gerant123!` | GERANT |
| `magasinier` | `magasin123!` | MAGASINIER |
| `vendeur` | `vendeur123!` | VENDEUR |
| `acheteur` | `acheteur123!` | ACHETEUR |
| `comptable` | `comptable123!` | COMPTABLE |
| `observateur` | `observateur123!` | OBSERVATEUR |

---

## API

Toutes les routes passent par la passerelle : `http://localhost:8222`.

```bash
# 1. Authentification
TOKEN=$(curl -s -X POST http://localhost:8222/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123!"}' | jq -r .token)

# 2. Appels authentifiés
curl -H "Authorization: Bearer $TOKEN" http://localhost:8222/api/v1/products
```

| Domaine | Base | Principales opérations |
|---|---|---|
| Authentification | `/api/v1/auth` | `login`, `register`, `forgot-password`, `reset-password`, `change-password`, `me` |
| Utilisateurs | `/api/v1/users` | liste, détail, suppression, `{id}/active`, `{id}/role` — **ADMIN** |
| Produits | `/api/v1/products` | CRUD, `purchase`, `restore` |
| Catégories | `/api/v1/categories` | CRUD |
| Fournisseurs | `/api/v1/suppliers` | CRUD |
| Mouvements | `/api/v1/stock-movements` | liste, `product/{id}`, création |
| Clients | `/api/v1/customers` | CRUD |
| Commandes | `/api/v1/orders`, `/api/v1/order-lines` | création, consultation |
| Paiements | `/api/v1/payments` | création |

Documentation détaillée : [`docs/API_DOCUMENTATION.md`](docs/API_DOCUMENTATION.md).
Collection Postman : [`docs/postman/`](docs/postman/).

---

## Développement hors Docker

Prérequis : JDK 17, Maven 3.9+, Node 20+.

```bash
# Infrastructure seule
docker compose up -d postgresql mongodb kafka zipkin mail-dev

# Backend (dans l'ordre)
cd backend
mvn -pl services/config-server spring-boot:run
mvn -pl services/discovery     spring-boot:run
mvn -pl services/gateway       spring-boot:run
# ... puis les services métier

# Frontend : voir le depot frontend-angular, clone a cote
cd ../frontend-angular && npm ci && npm start  # proxy vers la passerelle :8222
```

### Tests

```bash
cd backend && mvn test
```

20 tests couvrent la validation JWT de la passerelle, l'achat et la compensation
de stock, et la saga de création de commande.

### Schémas de base

Les schémas appartiennent à **Flyway** (`src/main/resources/db/migration`) et
Hibernate est en `ddl-auto: validate`. Toute évolution passe par une nouvelle
migration `V<n>__description.sql` ; ne modifiez jamais une migration déjà appliquée.

---

## Dépannage

**Un port est déjà utilisé.** Les ports exposés sur la machine sont paramétrables
dans `.env` — utile si un `ng serve` ou un autre projet occupe déjà 4200 ou 5433 :

```bash
FRONTEND_HOST_PORT=4201
POSTGRES_HOST_PORT=5434
```

**`database "stock" does not exist`.** Les bases ne sont créées qu'au tout premier
démarrage de PostgreSQL, sur un volume vide. Si un `up` précédent a échoué après
avoir initialisé le volume (conflit de port, par exemple), les scripts d'init sont
sautés. Repartez d'un volume neuf :

```bash
docker compose down -v && docker compose up -d
```

**`503 Service Unavailable` juste après le démarrage.** La passerelle n'a pas
encore rafraîchi son cache Eureka. Les instances apparaissent au bout de ~30 s ;
`http://localhost:8761` permet de vérifier qui est enregistré.

**Un service redémarre en boucle.** Consultez la cause exacte :

```bash
docker compose logs --tail 40 <service>
```

## Points connus

- **La sécurité repose sur la passerelle seule.** `stock-service`, `order-service`,
  `customer-service` et `payment-service` ne portent pas de chaîne de sécurité :
  ils font confiance à `X-User-Role`, que seule la passerelle est censée écrire.
  Un attaquant ayant pied sur le réseau Docker interne pourrait donc les appeler
  directement. Tant que seul le port `8222` est exposé, la surface reste fermée
  de l'extérieur, mais une défense en profondeur (validation du jeton dans chaque
  service, ou mTLS) reste à ajouter.

- **Couverture de tests partielle.** Les chemins critiques (authentification,
  concurrence sur le stock, saga) sont couverts, mais il n'existe pas encore de
  tests d'intégration bout en bout avec bases et Kafka réels.
- **Compensation best-effort.** Si `restore` échoue après un décrément de stock,
  l'incident est journalisé en ERROR mais n'est pas rejoué automatiquement.
  Un *circuit breaker* (Resilience4j) et une file de compensation restent à ajouter.
- **Pas de jeton de rafraîchissement.** Seul un jeton d'accès de 24 h existe.
- Les répertoires de pages Angular `produtos/`, `fornecedores/` et `usuarios/`
  conservent leur nom d'origine ; seuls le code et les contrats ont été harmonisés.

## Licence

Voir [LICENSE](LICENSE).
