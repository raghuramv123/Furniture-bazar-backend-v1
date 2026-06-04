package com.ram.demo.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.demo.dtos.CategoryRequest;
import com.ram.demo.entity.Category;
import com.ram.demo.exception.DuplicateResourceException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.CategoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // ── NEW: returns all categories flat ──
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by("displayOrder").ascending());
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found: " + slug));
    }

    public Category create(CategoryRequest req) {
        if (categoryRepository.existsBySlug(req.getSlug()))
            throw new DuplicateResourceException("Slug already exists: " + req.getSlug());

        Category category = new Category();
        category.setName(req.getName());
        category.setSlug(req.getSlug());
        category.setImageUrl(req.getImageUrl());
        category.setDisplayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0);
        category.setActive(true);  // ── explicitly set ──

        if (req.getParentId() != null) {
            Category parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    public Category update(Long id, CategoryRequest req) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found: " + id));

        cat.setName(req.getName());
        cat.setSlug(req.getSlug());
        cat.setImageUrl(req.getImageUrl());
        cat.setDisplayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0);

        if (req.getParentId() != null) {
            Category parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() ->
                        new ResourceNotFoundException("Parent category not found"));
            cat.setParent(parent);
        } else {
            cat.setParent(null);
        }
        return categoryRepository.save(cat);
    }
}