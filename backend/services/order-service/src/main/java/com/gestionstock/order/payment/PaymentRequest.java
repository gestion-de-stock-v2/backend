package com.gestionstock.order.payment;

import com.gestionstock.order.customer.CustomerResponse;
import com.gestionstock.order.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
    BigDecimal amount,
    PaymentMethod paymentMethod,
    Integer orderId,
    String orderReference,
    CustomerResponse customer
) {
}
