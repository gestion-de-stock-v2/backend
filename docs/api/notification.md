# notification-service

Port `8040` · MongoDB `notification` · **aucune API REST**

Service purement réactif : il consomme trois topics Kafka, historise chaque
notification en base et envoie l'e-mail correspondant.

| Topic | Type consommé | E-mail envoyé |
|---|---|---|
| `order-topic` | `OrderConfirmation` | Confirmation de commande |
| `payment-topic` | `PaymentConfirmation` | Confirmation de paiement |
| `password-reset-topic` | `PasswordResetNotification` | Lien de réinitialisation |

## Désérialisation

`spring.json.trusted.packages` est restreint à `com.gestionstock.*`. La valeur
précédente, `*`, autorisait la désérialisation de n'importe quelle classe présente
au classpath à partir d'un message Kafka.

## Envoi des e-mails

Templates Thymeleaf dans `src/main/resources/templates/` :
`order-confirmation.html`, `payment-confirmation.html`, `password-reset.html`.

L'expéditeur est configurable via `application.mail-from` (défaut
`no-reply@gestionstock.local`).

Un échec d'envoi est journalisé en WARN et **n'est pas propagé** au listener Kafka :
sans cela, le message serait rejoué en boucle et la notification dupliquée en base
à chaque tentative.

En développement, les e-mails sont consultables sur MailDev : http://localhost:1080
