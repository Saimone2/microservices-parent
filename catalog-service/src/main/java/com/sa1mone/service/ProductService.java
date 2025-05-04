package com.sa1mone.service;

import com.sa1mone.entity.Product;
import com.sa1mone.request.ProductRequest;
import com.sa1mone.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    void createProductPosition(ProductRequest productRequest);
    Product updateProduct(UUID productId, ProductRequest request);
    boolean isProductTableEmpty();
    List<Product> getTestProducts();
    boolean checkProductExists(UUID productId);
    void activateProduct(UUID productId);
    void deactivateProduct(UUID productId);
    List<ProductResponse> getActiveProducts();
    ProductResponse mapProductToResponse(Product product);
    void updateProductStock(UUID productId, int quantity);
    void reserveProductStock(UUID productId, int reservedQuantity);
    void restoreProductStock(UUID productId, int restoredQuantity);
}