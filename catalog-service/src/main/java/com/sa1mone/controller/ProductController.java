package com.sa1mone.controller;

import com.sa1mone.entity.Product;
import com.sa1mone.request.ProductRequest;
import com.sa1mone.response.ProductResponse;
import com.sa1mone.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestHeader(value = "X-Roles") String rolesHeader, @Valid @RequestBody ProductRequest request) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            productService.createProductPosition(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Product added successfully"
            ));
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/management/{productId}/exists")
    public boolean checkProductExists(@PathVariable UUID productId) {
        return productService.checkProductExists(productId);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts(@RequestHeader(value = "X-Roles") String rolesHeader) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(productService.getAllProducts());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID productId, @Valid @RequestBody ProductRequest request) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(productService.updateProduct(productId, request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<List<Product>> getTestProducts() {
        List<Product> testProducts = productService.getTestProducts();
        return ResponseEntity.ok(testProducts);
    }

    @PostMapping("/{productId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateProduct(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID productId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            productService.deactivateProduct(productId);
            return ResponseEntity.ok(Map.of("message", "Product deactivated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{productId}/activate")
    public ResponseEntity<Map<String, Object>> activateProduct(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID productId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            productService.activateProduct(productId);
            return ResponseEntity.ok(Map.of("message", "Product activated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}