package com.gestionstock.auth.dto;

import lombok.*;

/**
 * Reponse volontairement <strong>identique</strong> que le compte existe ou non, et
 * <strong>sans le jeton</strong>. L'implementation precedente renvoyait le jeton et le lien
 * de reinitialisation dans le corps HTTP : connaitre une adresse e-mail suffisait alors a
 * prendre le controle du compte correspondant, et la presence du jeton permettait d'enumerer
 * les comptes existants. Le lien est desormais transmis uniquement par e-mail.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ForgotPasswordResponse {
    private String message;
}
