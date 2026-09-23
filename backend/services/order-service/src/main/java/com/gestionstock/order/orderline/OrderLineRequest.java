package com.gestionstock.order.orderline;

public record OrderLineRequest(
        Integer id,
        Integer orderId,
        Integer productId,
        Integer quantity
) {
}
