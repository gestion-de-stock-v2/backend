package com.gestionstock.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank private String token;
    @NotBlank @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caracteres")
    private String newPassword;
}
