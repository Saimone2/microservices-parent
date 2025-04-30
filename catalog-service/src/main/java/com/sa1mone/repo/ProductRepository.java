package com.sa1mone.repo;

import com.sa1mone.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p ORDER BY p.createdAt ASC LIMIT 4")
    List<Product> findTestProducts();

    boolean existsByNameAndIdNot(String name, UUID id);
    List<Product> findByIsActiveTrue();
    boolean existsByName(String name);
}