package com.gestionstock.stock;

import com.gestionstock.stock.exception.ProductPurchaseException;
import com.gestionstock.stock.product.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository repository;
    @Mock private ProductMapper mapper;
    @InjectMocks private ProductService service;

    private Product product(int id, int quantity) {
        return Product.builder()
                .id(id).name("Produit " + id).availableQuantity(quantity)
                .price(BigDecimal.TEN).version(0L)
                .build();
    }

    /**
     * Une meme reference presente deux fois dans une commande doit voir ses quantites
     * additionnees. L'implementation d'origine appariait produits et lignes par position :
     * un doublon decalait l'appariement et decrementait le mauvais produit.
     */
    @Test
    void aggregatesDuplicateLinesForTheSameProduct() {
        Product p = product(1, 10);
        when(repository.findAllByIdInOrderByIdForUpdate(anyList())).thenReturn(List.of(p));
        when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toProductPurchaseResponse(any(), any()))
                .thenAnswer(inv -> new ProductPurchaseResponse(1, "Produit 1", null,
                        BigDecimal.TEN, inv.getArgument(1)));

        var result = service.purchaseProducts(List.of(
                new ProductPurchaseRequest(1, 3),
                new ProductPurchaseRequest(1, 4)));

        assertEquals(1, result.size());
        assertEquals(7, result.get(0).quantity(), "3 + 4 doivent etre additionnes");
        assertEquals(3, p.getAvailableQuantity(), "10 - 7");
    }

    @Test
    void rejectsPurchaseWhenStockIsInsufficient() {
        when(repository.findAllByIdInOrderByIdForUpdate(anyList()))
                .thenReturn(List.of(product(1, 2)));

        var request = List.of(new ProductPurchaseRequest(1, 5));
        var ex = assertThrows(ProductPurchaseException.class,
                () -> service.purchaseProducts(request));
        assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }

    @Test
    void rejectsPurchaseWhenAProductDoesNotExist() {
        when(repository.findAllByIdInOrderByIdForUpdate(anyList())).thenReturn(List.of());

        var request = List.of(new ProductPurchaseRequest(99, 1));
        assertThrows(ProductPurchaseException.class, () -> service.purchaseProducts(request));
    }

    @Test
    void restoreAddsBackTheAggregatedQuantity() {
        Product p = product(1, 5);
        when(repository.findAllByIdInOrderByIdForUpdate(anyList())).thenReturn(List.of(p));
        when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        service.restoreProducts(List.of(
                new ProductPurchaseRequest(1, 2),
                new ProductPurchaseRequest(1, 3)));

        assertEquals(10, p.getAvailableQuantity(), "5 + (2 + 3)");
    }
}
