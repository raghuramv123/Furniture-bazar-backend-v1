package com.ram.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    boolean existsBySlug(String slug);

    // ── for sorted flat list ──
    List<Category> findAll(Sort sort);
}