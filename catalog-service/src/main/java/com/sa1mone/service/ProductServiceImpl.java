package com.sa1mone.service;

import com.sa1mone.entity.Product;
import com.sa1mone.repo.ProductRepository;
import com.sa1mone.request.ProductRequest;
import com.sa1mone.response.ProductResponse;
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

        if (productRepository.existsByNameAndIdNot(request.getName(), productId)) {
            throw new IllegalArgumentException("Product from request already exists");
        }
        product.setName(request.getName());
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != 0) {
            product.setPrice(request.getPrice());
        }
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public boolean isProductTableEmpty() {
        return productRepository.count() == 0;
    }

    @Override
    public List<Product> getTestProducts() {
        return productRepository.findTestProducts();
    }

    @Override
    public boolean checkProductExists(UUID productId) {
        return productRepository.findById(productId).isPresent();
    }

    @Override
    public void activateProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getIsActive()) {
            throw new IllegalArgumentException("Product is already activated");
        }

        product.setIsActive(true);
        productRepository.save(product);
    }

    @Override
    public void deactivateProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is already deactivated");
        }

        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(product -> {
                    ProductResponse response = new ProductResponse();
                    response.setName(product.getName());
                    response.setDescription(product.getDescription());
                    response.setPrice(product.getPrice());
                    response.setQuantity(product.getStockQuantity());
                    return response;
                }).toList();
    }

    @Override
    public ProductResponse mapProductToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getStockQuantity());
        return response;
    }

    @Override
    public void updateProductStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException("Product not found")
        );
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    @Override
    public void reserveProductStock(UUID productId, int reservedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStockQuantity() < reservedQuantity) {
            throw new IllegalArgumentException("Not enough stock available!");
        }

        product.setStockQuantity(product.getStockQuantity() - reservedQuantity);
        productRepository.save(product);
    }

    @Override
    public void restoreProductStock(UUID productId, int restoredQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.setStockQuantity(product.getStockQuantity() + restoredQuantity);
        productRepository.save(product);
    }
}