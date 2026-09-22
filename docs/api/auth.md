# auth-service

Port `8085` · PostgreSQL `auth` · base path `/api/v1/auth` et `/api/v1/users`

## Authentification

### `POST /api/v1/auth/login` — public

```json
{ "username": "admin", "password": "admin123!" }
```

Réponse : `token`, `id`, `username`, `name`, `email`, `role`.
Identifiants erronés → `401` avec un message générique (« Identifiants invalides »),
qui ne révèle pas si le compte existe.

### `POST /api/v1/auth/register` — public

```json
{ "username": "jdupont", "password": "motdepasse8", "name": "Jean Dupont", "email": "j@x.io" }
```

**Aucun champ `role` n'est accepté.** Le compte est créé avec le rôle `OBSERVATEUR`
(lecture seule) quoi qu'envoie le client. Mot de passe : 8 caractères minimum.

### `POST /api/v1/auth/forgot-password` — public

```json
{ "email": "j@x.io" }
```

Réponse **toujours identique**, que le compte existe ou non :

```json
{ "message": "Si un compte existe avec cet e-mail, vous recevrez un lien de réinitialisation." }
```

Le jeton n'apparaît jamais dans la réponse. Il est haché en SHA-256 avant stockage
et le lien est transmis par e-mail via `password-reset-topic`. Validité : 30 minutes,
usage unique. En développement, l'e-mail est consultable sur MailDev (`:1080`).

### `POST /api/v1/auth/reset-password` — public

```json
{ "token": "<jeton reçu par e-mail>", "newPassword": "nouveaumotdepasse" }
```

### `POST /api/v1/auth/change-password` — authentifié

```json
{ "currentPassword": "…", "newPassword": "…" }
```

### `GET /api/v1/auth/me` — authentifié

Profil de l'utilisateur courant.

## Utilisateurs — `ADMIN` uniquement

| Méthode | Chemin | Effet |
|---|---|---|
| `GET` | `/api/v1/users` | Liste |
| `GET` | `/api/v1/users/{id}` | Détail |
| `DELETE` | `/api/v1/users/{id}` | Suppression |
| `PATCH` | `/api/v1/users/{id}/active` | Active / désactive |
| `PUT` | `/api/v1/users/{id}/role` | Attribue un rôle — `{"role":"GERANT"}` |

`PUT /{id}/role` est **l'unique voie d'élévation de privilèges**.
