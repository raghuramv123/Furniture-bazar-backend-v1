package com.ram.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ram.demo.dtos.ProductRequest;
import com.ram.demo.entity.Category;
import com.ram.demo.entity.Product;
import com.ram.demo.exception.DuplicateResourceException;
import com.ram.demo.exception.InsufficientStockException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.CategoryRepository;
import com.ram.demo.repository.ProductImageRepository;
import com.ram.demo.repository.ProductRepository;
import com.ram.demo.repository.ProductSpecification;

import jakarta.transaction.Transactional;

@Service
//	@RequiredArgsConstructor
	@Transactional
	public class ProductService {
		
		private final ProductRepository productRepository;
		private final CategoryRepository categoryRepository;
		private final ProductImageRepository productImageRepository;
		
		 @Autowired
		    public ProductService(ProductRepository productRepository,
		                          CategoryRepository categoryRepository,
		                          ProductImageRepository productImageRepository) {
		        this.productRepository      = productRepository;
		        this.categoryRepository     = categoryRepository;
		        this.productImageRepository = productImageRepository;
		    }
		public Page<Product> searchProducts(
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String material,
        String keyword,
        int page,
        int size,
        String sortBy,
        String sortDir) {

    Sort sort = sortDir.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

    Pageable pageable = PageRequest.of(page, size, sort);

    Specification<Product> spec = ProductSpecification
            .filter(categoryId, minPrice, maxPrice, material, keyword);

    return productRepository.findAll(spec, pageable);
}
		
		public Product getBySlug(String slug) {
			return productRepository.findBySlug(slug)
					.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));
		}
		
		public Product getById(Long id) {
			return productRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
		}
		
		public List<Product> getFeatured() {
			return productRepository.findByActiveTrueAndFeaturedTrue();
		}
		
		public Product create(ProductRequest req) {
		    // ── check duplicate SKU ──
		    if (productRepository.findBySku(req.getSku()).isPresent())
		        throw new DuplicateResourceException("SKU already exists: " + req.getSku());

		    Category category = categoryRepository.findById(req.getCategoryId())
		            .orElseThrow(() ->
		                new ResourceNotFoundException("Category not found: " + req.getCategoryId()));

		    Product product = new Product();
		    mapRequestToProduct(req, product, category);

		    // ── generate unique slug ──
		    String baseSlug = generateSlug(req.getName());
		    String slug = baseSlug;
		    int counter = 1;
		    while (productRepository.findBySlug(slug).isPresent()) {
		        slug = baseSlug + "-" + counter++;
		    }
		    product.setSlug(slug);

		    return productRepository.save(product);
		}
		public Product update(Long id, ProductRequest req) {
			Product product = getById(id);
			Category category = categoryRepository.findById(req.getCategoryId())
					.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
			mapRequestToProduct(req, product, category);
			return productRepository.save(product);
		}
		
		public void delete(Long id) {
			Product p = getById(id);
			p.setActive(false); // soft delete
			productRepository.save(p);
		}
		
		public void updateStock(Long id, int quantity) {
			Product p = getById(id);
			if (p.getStockQuantity() + quantity < 0)
				throw new InsufficientStockException("Not enough stock for: " + p.getName());
			p.setStockQuantity(p.getStockQuantity() + quantity);
			productRepository.save(p);
		}
		
		private void mapRequestToProduct(ProductRequest req, Product p, Category c) {
		    p.setName(req.getName());
		    p.setDescription(req.getDescription());
		    p.setPrice(req.getPrice());
		    p.setSalePrice(req.getSalePrice());
		    p.setStockQuantity(req.getStockQuantity());
		    p.setSku(req.getSku());
		    p.setCategory(c);
		    p.setMaterial(req.getMaterial());
		    p.setDimensions(req.getDimensions());
		    p.setWeightKg(req.getWeightKg());
		    p.setColor(req.getColor());
		    p.setFinish(req.getFinish());
		    p.setInstallationType(req.getInstallationType());
		    p.setFrameType(req.getFrameType());
		    p.setGlassType(req.getGlassType());
		    p.setWoodGrade(req.getWoodGrade());
		    p.setThickness(req.getThickness());
		    // ── handle null Boolean safely ──
		    p.setActive(req.getActive() != null ? req.getActive() : true);
		    p.setFeatured(req.getFeatured() != null ? req.getFeatured() : false);
		}
		
		private String generateSlug(String name) {
			return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
		}
	}
/*

// ── ProductService.java ───────────────────────────────────────────────

// ── CategoryService.java ──────────────────────────────────────────────


// ── CartService.java ──────────────────────────────────────────────────


// ── WishlistService.java ──────────────────────────────────────────────


// ── OrderService.java ─────────────────────────────────────────────────


// ── PaymentService.java ───────────────────────────────────────────────


// ── ReviewService.java ────────────────────────────────────────────────


// ── AddressService.java ───────────────────────────────────────────────


// ── AdminService.java ─────────────────────────────────────────────────

 */
