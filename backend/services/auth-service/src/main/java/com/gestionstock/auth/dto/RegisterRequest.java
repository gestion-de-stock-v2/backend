package com.gestionstock.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Inscription libre.
 * <p>
 * Ce DTO ne comporte <strong>volontairement pas</strong> de champ {@code role} : le role
 * est impose par le serveur ({@code Role.OBSERVATEUR}). Auparavant le client pouvait
 * fournir {@code "role": "ADMIN"} sur un endpoint public et obtenir un compte
 * administrateur sans aucune authentification. L'attribution d'un role passe desormais
 * par {@code PUT /api/v1/users/{id}/role}, reserve aux ADMIN.
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 50) private String username;
    @NotBlank @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caracteres")
    private String password;
    @NotBlank private String name;
    @NotBlank @Email private String email;
}
