package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ram.demo.entity.ProductImage;



public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    void deleteByProductId(Long productId);
}