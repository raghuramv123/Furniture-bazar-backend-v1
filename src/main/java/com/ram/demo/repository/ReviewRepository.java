package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // ── FIXED: isApproved → approved (derived method) ──
    List<Review> findByProductIdAndApprovedTrue(Long productId);

    Optional<Review> findByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long userId);

    // ── FIXED: remove @Query entirely — use derived method ──
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Double averageRatingByProductId(@Param("productId") Long productId);
}