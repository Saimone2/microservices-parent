package com.sa1mone.controller;

import com.sa1mone.entity.Product;
import com.sa1mone.response.ProductResponse;
import com.sa1mone.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/management/product")
public class ProductManagementController {

    private final ProductService productService;

    public ProductManagementController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/find-product-by-id")
    public ResponseEntity<?> getProductById(@RequestParam("id") UUID id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Product not found"
            ));
        }
        ProductResponse productResponse = productService.mapProductToResponse(product);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/{productId}/exists")
    public boolean checkProductExists(@PathVariable UUID productId) {
        return productService.checkProductExists(productId);
    }

    @PostMapping("/batch-full")
    public ResponseEntity<Map<UUID, ProductResponse>> getProductsBatch(@RequestBody Set<UUID> batchRequest) {
        if (batchRequest.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Map<UUID, ProductResponse> products = productService.getProductsBatch(batchRequest);
        return ResponseEntity.ok(products);
    }
}