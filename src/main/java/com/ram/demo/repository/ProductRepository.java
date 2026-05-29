package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Product;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long>,
                JpaSpecificationExecutor<Product> {   // ← add this

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByActiveTrueAndFeaturedTrue();
    List<Product> findByStockQuantityLessThanEqualAndActiveTrue(int stockQuantity);
}