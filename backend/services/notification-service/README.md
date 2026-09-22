# notification-service

Envoi des e-mails transactionnels et historisation des notifications.

| | |
|---|---|
| Port | `8040` |
| Stockage | MongoDB `notification` |
| Configuration | `backend/services/config-server/src/main/resources/configurations/notification-service.yml` |

## Endpoints

- Consomme `order-topic` → e-mail de confirmation de commande
- Consomme `payment-topic` → e-mail de confirmation de paiement
- Consomme `password-reset-topic` → lien de réinitialisation de mot de passe
- En développement, les e-mails sont consultables sur MailDev : http://localhost:1080

## Démarrage

```bash
# via la pile complète (recommandé)
docker compose up notification-service

# isolément, config-server et discovery devant tourner
cd backend && mvn -pl services/notification-service spring-boot:run
```

Ce service est accessible **via la passerelle** (`:8222`), qui exige un jeton JWT valide.
