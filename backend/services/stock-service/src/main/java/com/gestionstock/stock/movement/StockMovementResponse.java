package com.gestionstock.stock.movement;

import java.time.LocalDateTime;

public record StockMovementResponse(
        Integer id,
        MovementType type,
        Integer quantity,
        String note,
        LocalDateTime createdAt,
        Integer productId,
        String productName,
        Integer availableQuantityAfter
) {}
