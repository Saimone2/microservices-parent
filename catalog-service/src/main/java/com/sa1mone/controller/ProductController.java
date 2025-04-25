package com.sa1mone.controller;

import com.sa1mone.entity.Product;
import com.sa1mone.request.ProductRequest;
import com.sa1mone.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductRequest request) {
        productService.createProductPosition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Product added successfully"
        ));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/{productId}/exists")
    public ResponseEntity<Product> checkProductExists(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.checkProductExists(productId));
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID productId, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @GetMapping("/test")
    public ResponseEntity<List<Product>> getTestProducts() {
        List<Product> testProducts = productService.getTestProducts();
        return ResponseEntity.ok(testProducts);
    }
}