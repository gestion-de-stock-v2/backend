package com.gestionstock.stock.movement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService service;

    @GetMapping
    public List<StockMovementResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/product/{product-id}")
    public List<StockMovementResponse> findByProduct(@PathVariable("product-id") Integer productId) {
        return service.findByProduct(productId);
    }

    @PostMapping
    public ResponseEntity<StockMovementResponse> register(
            @Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(service.register(request));
    }
}
