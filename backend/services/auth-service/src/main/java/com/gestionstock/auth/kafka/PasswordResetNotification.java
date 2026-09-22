package com.gestionstock.auth.kafka;

/** Evenement consomme par notification-service pour envoyer le lien par e-mail. */
public record PasswordResetNotification(
        String email,
        String username,
        String resetLink,
        int expiryMinutes
) {}
