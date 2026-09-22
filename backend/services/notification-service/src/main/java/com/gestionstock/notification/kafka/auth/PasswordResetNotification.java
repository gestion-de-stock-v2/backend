package com.gestionstock.notification.kafka.auth;

/**
 * Demande de reinitialisation de mot de passe emise par auth-service.
 * Le lien contient le jeton en clair : il ne transite que par e-mail, jamais dans une
 * reponse HTTP.
 */
public record PasswordResetNotification(
        String email,
        String username,
        String resetLink,
        int expiryMinutes
) {}
