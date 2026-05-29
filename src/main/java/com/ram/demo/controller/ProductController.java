package com.ram.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.ProductRequest;
import com.ram.demo.entity.Product;
import com.ram.demo.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<Product>> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return ResponseEntity.ok(productService.searchProducts(
                categoryId, minPrice, maxPrice, material, keyword,
                page, size, sortBy, sortDir));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Product>> getFeatured() {
        return ResponseEntity.ok(productService.getFeatured());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Product> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id,
                                           @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
/*
// ── ProductController.java ────────────────────────────────────────────


// ── CategoryController.java ───────────────────────────────────────────


// ── CartController.java ───────────────────────────────────────────────


// ── WishlistController.java ───────────────────────────────────────────

// ── OrderController.java ──────────────────────────────────────────────

// ── PaymentController.java ────────────────────────────────────────────


// ── ReviewController.java ─────────────────────────────────────────────

// ── AddressController.java ────────────────────────────────────────────


// ── AdminController.java ──────────────────────────────────────────────

 */