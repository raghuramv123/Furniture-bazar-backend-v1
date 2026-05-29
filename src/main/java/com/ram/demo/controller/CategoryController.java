package com.ram.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.CategoryRequest;
import com.ram.demo.entity.Category;
import com.ram.demo.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ── returns ALL categories flat — for dropdowns ──
    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // ── returns only root categories ──
    @GetMapping("/roots")
    public ResponseEntity<List<Category>> getRoots() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Category>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getChildren(parentId));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> update(@PathVariable Long id,
                                            @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }
}