package com.sa1mone.controller;

import com.sa1mone.entity.Product;
import com.sa1mone.response.ProductResponse;
import com.sa1mone.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
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
}