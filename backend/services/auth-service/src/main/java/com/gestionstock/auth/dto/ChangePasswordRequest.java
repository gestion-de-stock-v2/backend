package com.gestionstock.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank private String currentPassword;
    @NotBlank @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caracteres")
    private String newPassword;
}
