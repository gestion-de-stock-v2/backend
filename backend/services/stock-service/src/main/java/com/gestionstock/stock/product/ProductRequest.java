package com.gestionstock.stock.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        Integer id,
        @NotBlank(message = "Le nom du produit est obligatoire")
        String name,
        String description,
        @NotNull(message = "La quantite disponible est obligatoire")
        @PositiveOrZero(message = "La quantite disponible ne peut pas etre negative")
        Integer availableQuantity,
        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit etre strictement positif")
        BigDecimal price,
        @NotNull(message = "La categorie du produit est obligatoire")
        Integer categoryId,
        Integer supplierId
) {}
