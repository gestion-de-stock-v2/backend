package com.gestionstock.stock.product;

import com.gestionstock.stock.category.Category;
import com.gestionstock.stock.supplier.Supplier;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {

    public Product toProduct(ProductRequest request) {
        return Product.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .availableQuantity(request.availableQuantity())
                .price(request.price())
                .category(Category.builder().id(request.categoryId()).build())
                .supplier(request.supplierId() == null
                        ? null
                        : Supplier.builder().id(request.supplierId()).build())
                .build();
    }

    public ProductResponse toProductResponse(Product product) {
        Category category = product.getCategory();
        Supplier supplier = product.getSupplier();
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity(),
                product.getPrice(),
                category == null ? null : category.getId(),
                category == null ? null : category.getName(),
                category == null ? null : category.getDescription(),
                supplier == null ? null : supplier.getId(),
                supplier == null ? null : supplier.getName()
        );
    }

    public ProductPurchaseResponse toProductPurchaseResponse(Product product, Integer quantity) {
        return new ProductPurchaseResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }
}
