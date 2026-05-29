package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ram.demo.entity.WishList;

public interface WishlistRepository extends JpaRepository<WishList, Long> {
    List<WishList> findByUserId(Long userId);
    Optional<WishList> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}