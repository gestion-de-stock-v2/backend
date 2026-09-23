package com.gestionstock.order;

import com.gestionstock.order.customer.CustomerClient;
import com.gestionstock.order.customer.CustomerResponse;
import com.gestionstock.order.exception.BusinessException;
import com.gestionstock.order.kafka.OrderProducer;
import com.gestionstock.order.order.*;
import com.gestionstock.order.orderline.OrderLineService;
import com.gestionstock.order.payment.PaymentClient;
import com.gestionstock.order.product.ProductClient;
import com.gestionstock.order.product.PurchaseRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Couvre la compensation de la saga de commande : le stock est decremente par un appel
 * synchrone a stock-service, hors de la transaction JPA locale. Un rollback Spring ne
 * l'annule donc pas et toute erreur survenant ensuite doit declencher une compensation
 * explicite, sous peine de "perdre" du stock disponible.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock private OrderRepository repository;
    @Mock private OrderMapper mapper;
    @Mock private CustomerClient customerClient;
    @Mock private PaymentClient paymentClient;
    @Mock private ProductClient productClient;
    @Mock private OrderLineService orderLineService;
    @Mock private OrderProducer orderProducer;
    @InjectMocks private OrderService service;

    private OrderRequest request() {
        return new OrderRequest(null, "REF-1", BigDecimal.TEN, PaymentMethod.PAYPAL,
                "cust-1", List.of(new PurchaseRequest(1, 2)));
    }

    private void givenCustomerAndStockAvailable() {
        when(customerClient.findCustomerById("cust-1"))
                .thenReturn(Optional.of(new CustomerResponse("cust-1", "Ada", "Lovelace", "ada@x.io")));
        when(productClient.purchaseProducts(anyList())).thenReturn(List.of());
        Order saved = Order.builder().id(42).reference("REF-1").build();
        when(mapper.toOrder(any())).thenReturn(saved);
        when(repository.save(any())).thenReturn(saved);
    }

    @Test
    void createsOrderAndPublishesConfirmation() {
        givenCustomerAndStockAvailable();

        Integer id = service.createOrder(request());

        assertEquals(42, id);
        verify(orderProducer).sendOrderConfirmation(any());
        verify(productClient, never()).restoreStock(anyList());
    }

    /** Client inconnu : le stock ne doit pas avoir ete touche, donc rien a compenser. */
    @Test
    void failsFastWhenCustomerDoesNotExist() {
        when(customerClient.findCustomerById("cust-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.createOrder(request()));

        verify(productClient, never()).purchaseProducts(anyList());
        verify(productClient, never()).restoreStock(anyList());
    }

    /**
     * Echec du paiement apres decrement du stock : la compensation doit etre declenchee
     * et l'exception d'origine doit remonter intacte.
     */
    @Test
    void compensatesStockWhenPaymentFails() {
        givenCustomerAndStockAvailable();
        when(paymentClient.requestOrderPayment(any()))
                .thenThrow(new IllegalStateException("paiement refuse"));

        var ex = assertThrows(IllegalStateException.class, () -> service.createOrder(request()));

        assertEquals("paiement refuse", ex.getMessage(),
                "l'exception d'origine ne doit pas etre masquee par la compensation");
        verify(productClient).restoreStock(request().products());
        verify(orderProducer, never()).sendOrderConfirmation(any());
    }

    /** Meme exigence si c'est la sauvegarde de la commande qui echoue. */
    @Test
    void compensatesStockWhenOrderPersistenceFails() {
        givenCustomerAndStockAvailable();
        when(repository.save(any())).thenThrow(new RuntimeException("base indisponible"));

        assertThrows(RuntimeException.class, () -> service.createOrder(request()));

        verify(productClient).restoreStock(request().products());
    }
}
