package com.ram.demo.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @SequenceGenerator(name = "categories_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "categories_id", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String slug;

    // ── FIXED: ignore 'children' when serializing parent ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"children", "parent"})
    private Category parent;

    // ── FIXED: ignore 'parent' when serializing children ──
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"parent", "children"})
    private List<Category> children;

    private String imageUrl;
    private Integer displayOrder;

    @Column(name = "is_active")
    private boolean active;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore   // ── never serialize products inside category ──
    private List<Product> products;
}