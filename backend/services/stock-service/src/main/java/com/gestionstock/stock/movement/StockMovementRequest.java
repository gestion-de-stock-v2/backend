package com.gestionstock.stock.movement;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockMovementRequest(
        @NotNull(message = "Le produit est obligatoire") Integer productId,
        @NotNull(message = "Le type de mouvement est obligatoire") MovementType type,
        @Positive(message = "La quantite doit etre strictement positive") Integer quantity,
        String note
) {}
