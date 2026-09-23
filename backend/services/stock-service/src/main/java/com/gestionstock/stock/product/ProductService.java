package com.gestionstock.stock.product;

import com.gestionstock.stock.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Transactional
    public Integer createProduct(ProductRequest request) {
        return repository.save(mapper.toProduct(request)).getId();
    }

    @Transactional
    public ProductResponse updateProduct(Integer id, ProductRequest request) {
        Product existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
        Product incoming = mapper.toProduct(request);
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setAvailableQuantity(incoming.getAvailableQuantity());
        existing.setPrice(incoming.getPrice());
        existing.setCategory(incoming.getCategory());
        existing.setSupplier(incoming.getSupplier());
        return mapper.toProductResponse(repository.save(existing));
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Produit introuvable : " + id);
        }
        repository.deleteById(id);
    }

    public ProductResponse findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable : " + id));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(mapper::toProductResponse).toList();
    }

    /**
     * Decremente le stock des produits achetes, sous verrou pessimiste.
     * <p>
     * Les lignes sont d'abord regroupees par produit : l'implementation precedente appariait
     * la liste des produits charges et la liste des lignes demandees par position, ce qui
     * desynchronisait l'appariement des qu'un meme produit apparaissait deux fois dans la
     * commande.
     */
    @Transactional
    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        Map<Integer, Integer> quantityByProductId = aggregateByProduct(request);

        List<Product> storedProducts =
                repository.findAllByIdInOrderByIdForUpdate(List.copyOf(quantityByProductId.keySet()));

        if (storedProducts.size() != quantityByProductId.size()) {
            throw new ProductPurchaseException("Un ou plusieurs produits demandes n'existent pas");
        }

        var purchased = new ArrayList<ProductPurchaseResponse>();
        for (Product product : storedProducts) {
            int requested = quantityByProductId.get(product.getId());
            if (product.getAvailableQuantity() < requested) {
                throw new ProductPurchaseException(
                        "Stock insuffisant pour le produit " + product.getId()
                      + " (disponible : " + product.getAvailableQuantity()
                      + ", demande : " + requested + ")");
            }
            product.setAvailableQuantity(product.getAvailableQuantity() - requested);
            repository.save(product);
            purchased.add(mapper.toProductPurchaseResponse(product, requested));
        }
        return purchased;
    }

    /**
     * Compensation (saga) d'un {@link #purchaseProducts} : reincremente le stock.
     * Appelee par order-service quand une etape posterieure au decrement echoue.
     * <p>
     * Best-effort : un produit qui n'existe plus est journalise en WARN plutot que de
     * faire echouer toute la compensation.
     */
    @Transactional
    public void restoreProducts(List<ProductPurchaseRequest> request) {
        Map<Integer, Integer> quantityByProductId = aggregateByProduct(request);

        Map<Integer, Product> storedProducts =
                repository.findAllByIdInOrderByIdForUpdate(List.copyOf(quantityByProductId.keySet()))
                        .stream()
                        .collect(Collectors.toMap(Product::getId, Function.identity()));

        quantityByProductId.forEach((productId, quantity) -> {
            Product product = storedProducts.get(productId);
            if (product == null) {
                log.warn("Compensation impossible pour le produit {} : il n'existe plus", productId);
                return;
            }
            product.setAvailableQuantity(product.getAvailableQuantity() + quantity);
            repository.save(product);
        });
    }

    /** Regroupe les lignes par produit et additionne les quantites (commande dedoublonnee). */
    private Map<Integer, Integer> aggregateByProduct(List<ProductPurchaseRequest> request) {
        if (request == null || request.isEmpty()) {
            throw new ProductPurchaseException("La liste des produits est vide");
        }
        Map<Integer, Integer> aggregated = new LinkedHashMap<>();
        for (ProductPurchaseRequest line : request) {
            aggregated.merge(line.productId(), line.quantity(), Integer::sum);
        }
        return aggregated;
    }
}
