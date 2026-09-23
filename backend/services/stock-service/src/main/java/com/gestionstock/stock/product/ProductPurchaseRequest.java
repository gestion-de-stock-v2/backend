package com.gestionstock.stock.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductPurchaseRequest(
        @NotNull(message = "L'identifiant du produit est obligatoire")
        Integer productId,
        @Positive(message = "La quantite doit etre strictement positive")
        Integer quantity
) {}
