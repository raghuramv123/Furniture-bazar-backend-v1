package com.ram.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // ── make sure this method exists ──
    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}