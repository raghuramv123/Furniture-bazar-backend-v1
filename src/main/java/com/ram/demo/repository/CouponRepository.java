package com.ram.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // ── FIXED: isActive → active ──
    Optional<Coupon> findByCodeAndActiveTrue(String code);

    boolean existsByCode(String code);
}