package com.gestionstock.notification.email;

import lombok.Getter;

@Getter
public enum EmailTemplates {

    PAYMENT_CONFIRMATION("payment-confirmation.html", "Paiement confirme"),
    ORDER_CONFIRMATION("order-confirmation.html", "Confirmation de votre commande"),
    PASSWORD_RESET("password-reset.html", "Reinitialisation de votre mot de passe");

    private final String template;
    private final String subject;

    EmailTemplates(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }
}
