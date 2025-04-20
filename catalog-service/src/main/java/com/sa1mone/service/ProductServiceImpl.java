package com.sa1mone.service;

import com.sa1mone.entity.Product;
import com.sa1mone.repo.ProductRepository;
import com.sa1mone.request.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void createProductPosition(ProductRequest productRequest) {
        if (productRepository.existsByName(productRequest.getName())) {
            throw new IllegalArgumentException("This product already exists");
        }

        Product product = new Product();

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(0);

        LocalDateTime createTime = LocalDateTime.now();
        product.setCreatedAt(createTime);
        product.setUpdatedAt(createTime);

        System.out.println(product);
        productRepository.save(product);
    }

    @Override
    public Product updateProduct(UUID productId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Product from request already exists");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        return productRepository.save(product);
    }
}