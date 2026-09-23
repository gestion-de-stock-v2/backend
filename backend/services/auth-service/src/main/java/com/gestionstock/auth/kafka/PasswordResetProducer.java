package com.gestionstock.auth.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetProducer {

    private final KafkaTemplate<String, PasswordResetNotification> kafkaTemplate;

    public void sendPasswordResetLink(PasswordResetNotification notification) {
        log.info("Publication d'une demande de reinitialisation de mot de passe");
        Message<PasswordResetNotification> message = MessageBuilder
                .withPayload(notification)
                .setHeader(org.springframework.kafka.support.KafkaHeaders.TOPIC, "password-reset-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
