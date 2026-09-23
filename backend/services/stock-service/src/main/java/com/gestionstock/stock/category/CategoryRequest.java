package com.gestionstock.stock.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        Integer id,
        @NotBlank(message = "Le nom de la categorie est obligatoire") String name,
        String description
) {}
