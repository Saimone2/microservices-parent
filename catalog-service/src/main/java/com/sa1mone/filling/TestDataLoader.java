package com.sa1mone.filling;

import com.sa1mone.request.ProductRequest;
import com.sa1mone.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final ProductService productService;

    public TestDataLoader(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) {
        if (productService.isProductTableEmpty()) {
            List<ProductRequest> testProducts = List.of(
                    new ProductRequest("Laptop", "High-end gaming laptop", 1500.0),
                    new ProductRequest("Smartphone", "Latest model smartphone", 999.99),
                    new ProductRequest("Headphones", "Noise-cancelling headphones", 199.99),
                    new ProductRequest("Monitor", "4K Ultra HD monitor", 499.99),
                    new ProductRequest("Mechanical Keyboard", "RGB backlit, programmable keys", 129.99)
            );
            testProducts.forEach(this::addTestProduct);
        }
    }

    private void addTestProduct(ProductRequest productRequest) {
        productService.createProductPosition(productRequest);
    }
}