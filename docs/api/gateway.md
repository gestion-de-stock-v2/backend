# gateway

Port `8222` · point d'entrée unique du système

## Sécurité

Un `GlobalFilter` (`JwtAuthenticationGatewayFilter`, ordre `-100`) s'exécute avant
le routage et rejette au plus tôt toute requête non authentifiée.

Chemins publics :

```
/api/v1/auth/login   /api/v1/auth/register
/api/v1/auth/forgot-password   /api/v1/auth/reset-password
/actuator/health   /actuator/info
```

Pour tout le reste, un `Authorization: Bearer <jeton>` valide est exigé. Le filtre
vérifie la signature et l'expiration, puis ajoute `X-User-Name` et `X-User-Role`.

**Ces deux en-têtes sont effacés de la requête entrante avant d'être réécrits**,
y compris sur les chemins publics : un client ne peut pas se déclarer administrateur
en les fournissant lui-même.

`jwt.secret` doit être **identique** à celui d'`auth-service` ; la passerelle refuse
de démarrer s'il est absent ou fait moins de 32 octets.

## Routage

`spring.cloud.gateway.discovery.locator` est **désactivé**. Activé, il publiait
automatiquement tout service Eureka sous `/<service-id>/**` — dont le config-server,
ce qui exposait les identifiants de toutes les bases via
`/config-server/order-service/default`. Seules les routes ci-dessous existent.

| Route | Cible | Prédicat |
|---|---|---|
| auth | `lb://AUTH-SERVICE` | `/api/v1/auth/**` |
| users | `lb://AUTH-SERVICE` | `/api/v1/users/**` |
| products | `lb://STOCK-SERVICE` | `/api/v1/products/**` |
| categories | `lb://STOCK-SERVICE` | `/api/v1/categories/**` |
| suppliers | `lb://STOCK-SERVICE` | `/api/v1/suppliers/**` |
| stock-movements | `lb://STOCK-SERVICE` | `/api/v1/stock-movements/**` |
| customers | `lb://CUSTOMER-SERVICE` | `/api/v1/customers/**` |
| orders | `lb://ORDER-SERVICE` | `/api/v1/orders/**` |
| order-lines | `lb://ORDER-SERVICE` | `/api/v1/order-lines/**` |
| payments | `lb://PAYMENT-SERVICE` | `/api/v1/payments/**` |

## CORS

Origine restreinte à `FRONTEND_URL` (défaut `http://localhost:4200`), avec
`allowCredentials: true`. La configuration précédente acceptait toute origine.

## Agrégation Swagger

Aucune : il n'existe pas de dépendance `springdoc-openapi` dans le projet.
La documentation de référence est [`API_DOCUMENTATION.md`](../API_DOCUMENTATION.md).
