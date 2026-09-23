package com.gestionstock.stock.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findAllByIdInOrderById(List<Integer> ids);

    /**
     * Variante verrouillante utilisee par le chemin d'achat et sa compensation.
     * <p>
     * Le verrou pessimiste serialise les transactions concurrentes portant sur les memes
     * produits. Sans lui, deux commandes simultanees lisaient la meme quantite disponible
     * et la decrementaient chacune, ce qui permettait de vendre deux fois le meme stock.
     * Les identifiants sont ordonnes pour que toutes les transactions prennent leurs
     * verrous dans le meme ordre et ne puissent pas s'interbloquer.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids order by p.id")
    List<Product> findAllByIdInOrderByIdForUpdate(@Param("ids") List<Integer> ids);
}
