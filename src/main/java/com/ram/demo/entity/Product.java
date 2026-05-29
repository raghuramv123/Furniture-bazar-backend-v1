package com.ram.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Products")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Product 
{

	@Id
	@NonNull
	@SequenceGenerator(name="product_id",initialValue = 1,allocationSize = 1)
	@GeneratedValue(generator = "product_id",strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(unique = true)
	private String slug;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(nullable = false,precision = 10,scale=2)
	private BigDecimal price;
	
	@Column(precision = 10,scale = 2)
	private BigDecimal salePrice;
	
	private Integer stockQuantity;
	
	@Column(unique = true)
	private String sku;
	
	// in Product.java — category field
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	@JsonIgnoreProperties({"children", "parent", "products"})
	private Category category;
	
	// Furniture-specific fields
    private String material;       // "Teak Wood", "Tempered Glass", etc.
    private String dimensions;     // "200x80x75 cm"
    private BigDecimal weightKg;
    private String color;
    private String finish;         // "Matte", "Gloss", "Natural"
    private String installationType; // "Wall-mounted", "Free-standing"
    private String frameType;      // for doors/windows: "Aluminum", "UPVC"
    private String glassType;      // for glass walls/windows
    private String woodGrade;      // for plywood: "BWP", "MR", "BR"
    private Integer thickness;     // in mm for plywood/glass

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_featured")
    private boolean featured;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp  private LocalDateTime updatedAt;

}
