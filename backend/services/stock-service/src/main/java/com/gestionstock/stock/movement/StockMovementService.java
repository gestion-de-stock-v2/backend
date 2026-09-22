package com.gestionstock.stock.movement;

import com.gestionstock.stock.exception.InsufficientStockException;
import com.gestionstock.stock.product.Product;
import com.gestionstock.stock.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository repository;
    private final ProductRepository productRepository;

    public List<StockMovementResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<StockMovementResponse> findByProduct(Integer productId) {
        return repository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(this::toResponse).toList();
    }

    /**
     * Enregistre un mouvement et ajuste la quantite disponible du produit.
     * Le produit est charge sous verrou pessimiste afin que deux mouvements concurrents
     * ne puissent pas partir de la meme quantite de depart.
     */
    @Transactional
    public StockMovementResponse register(StockMovementRequest request) {
        Product product = productRepository
                .findAllByIdInOrderByIdForUpdate(List.of(request.productId()))
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Produit introuvable : " + request.productId()));

        if (request.type() == MovementType.EXIT) {
            if (product.getAvailableQuantity() < request.quantity()) {
                throw new InsufficientStockException(
                        "Stock insuffisant. Disponible : " + product.getAvailableQuantity());
            }
            product.setAvailableQuantity(product.getAvailableQuantity() - request.quantity());
        } else {
            product.setAvailableQuantity(product.getAvailableQuantity() + request.quantity());
        }
        productRepository.save(product);

        StockMovement movement = repository.save(StockMovement.builder()
                .type(request.type())
                .quantity(request.quantity())
                .note(request.note())
                .product(product)
                .build());

        return toResponse(movement);
    }

    private StockMovementResponse toResponse(StockMovement m) {
        return new StockMovementResponse(
                m.getId(), m.getType(), m.getQuantity(), m.getNote(), m.getCreatedAt(),
                m.getProduct().getId(), m.getProduct().getName(),
                m.getProduct().getAvailableQuantity());
    }
}
