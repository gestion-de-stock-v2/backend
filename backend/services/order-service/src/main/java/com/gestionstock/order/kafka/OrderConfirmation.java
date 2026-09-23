package com.gestionstock.order.kafka;

import com.gestionstock.order.customer.CustomerResponse;
import com.gestionstock.order.order.PaymentMethod;
import com.gestionstock.order.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
