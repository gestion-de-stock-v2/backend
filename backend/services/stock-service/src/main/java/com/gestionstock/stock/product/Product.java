package com.gestionstock.stock.product;

import com.gestionstock.stock.category.Category;
import com.gestionstock.stock.supplier.Supplier;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Produit en stock. Entite unique du systeme : elle fusionne le produit du domaine
 * e-commerce et le produit du domaine gestion de stock, qui coexistaient auparavant dans
 * deux services distincts avec deux quantites disponibles divergentes.
 */
@Entity
@Table(name = "product")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Quantite disponible, en unites entieres. Protegee par le verrou optimiste
     * {@link #version} : sans lui, deux commandes concurrentes lisaient la meme valeur et
     * la decrementaient chacune de leur cote, ce qui permettait de vendre deux fois le
     * meme stock.
     */
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(precision = 19, scale = 2)
    private BigDecimal price;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
