package com.ram.demo.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.ram.demo.entity.Product;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String material,
            String keyword) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // always filter active products
            predicates.add(cb.isTrue(root.get("active")));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (material != null && !material.isBlank()) {
                predicates.add(cb.equal(root.get("material"), material));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}