package com.gestionstock.stock.supplier;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        Integer id,
        @NotBlank(message = "Le nom du fournisseur est obligatoire") String name,
        String registrationNumber,
        String phone,
        @Email(message = "E-mail invalide") String email
) {}
