package com.gestionstock.stock.movement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {
    List<StockMovement> findByProductIdOrderByCreatedAtDesc(Integer productId);

    List<StockMovement> findAllByOrderByCreatedAtDesc();
}
