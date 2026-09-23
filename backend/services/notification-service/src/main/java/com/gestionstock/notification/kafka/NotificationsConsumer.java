package com.gestionstock.notification.kafka;

import com.gestionstock.notification.email.EmailService;
import com.gestionstock.notification.kafka.order.OrderConfirmation;
import com.gestionstock.notification.kafka.auth.PasswordResetNotification;
import com.gestionstock.notification.kafka.payment.PaymentConfirmation;
import com.gestionstock.notification.notification.Notification;
import com.gestionstock.notification.notification.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.gestionstock.notification.notification.NotificationType.ORDER_CONFIRMATION;
import static com.gestionstock.notification.notification.NotificationType.PASSWORD_RESET;
import static com.gestionstock.notification.notification.NotificationType.PAYMENT_CONFIRMATION;
import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;
    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotifications(PaymentConfirmation paymentConfirmation) throws MessagingException {
        log.info(format("Consuming the message from payment-topic Topic:: %s", paymentConfirmation));
        repository.save(
                Notification.builder()
                        .type(PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );
        var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
        emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderReference()
        );
    }

    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmation));
        repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sendOrderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }

    /**
     * Transmet par e-mail le lien de reinitialisation emis par auth-service.
     * Le jeton ne transite que par ce canal.
     */
    @KafkaListener(topics = "password-reset-topic")
    public void consumePasswordResetNotifications(PasswordResetNotification notification)
            throws MessagingException {
        log.info("Reception d'une demande de reinitialisation de mot de passe");
        repository.save(
                Notification.builder()
                        .type(PASSWORD_RESET)
                        .notificationDate(LocalDateTime.now())
                        .passwordReset(notification)
                        .build()
        );
        emailService.sendPasswordResetEmail(
                notification.email(),
                notification.username(),
                notification.resetLink(),
                notification.expiryMinutes()
        );
    }
}
