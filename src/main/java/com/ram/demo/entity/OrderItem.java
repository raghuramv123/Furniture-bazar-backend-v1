package com.ram.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="order_items")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class OrderItem {

	@Id
	@SequenceGenerator(name="order_item_id",initialValue = 1,allocationSize = 1)
	@GeneratedValue(generator = "order_item_id",strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="order_id",nullable = false)
	private Order order;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(nullable = false,precision = 10,scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false,precision = 10,scale = 2)
	private BigDecimal totalPrice;
	
	// Snapshot of product at time of order (name, SKU, dimensions)
    @Column(columnDefinition = "TEXT")
    private String productSnapshot; // stored as JSON string
}
