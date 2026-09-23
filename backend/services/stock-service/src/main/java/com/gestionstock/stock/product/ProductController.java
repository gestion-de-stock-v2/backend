package com.gestionstock.stock.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable("product-id") Integer productId) {
        return ResponseEntity.ok(service.findById(productId));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(service.createProduct(request));
    }

    @PutMapping("/{product-id}")
    public ResponseEntity<ProductResponse> update(@PathVariable("product-id") Integer productId,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(service.updateProduct(productId, request));
    }

    @DeleteMapping("/{product-id}")
    public ResponseEntity<Void> delete(@PathVariable("product-id") Integer productId) {
        service.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    /** Appele par order-service lors de la creation d'une commande. */
    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponse>> purchase(
            @RequestBody List<ProductPurchaseRequest> request) {
        return ResponseEntity.ok(service.purchaseProducts(request));
    }

    /** Compensation appelee par order-service quand une commande echoue apres le decrement. */
    @PostMapping("/restore")
    public ResponseEntity<Void> restore(@RequestBody List<ProductPurchaseRequest> request) {
        service.restoreProducts(request);
        return ResponseEntity.ok().build();
    }
}
