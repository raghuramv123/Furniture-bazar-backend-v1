package com.ram.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name="cart_items")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class CartItem {
	
	@Id
	@SequenceGenerator(name="cart_item_id",initialValue = 1,allocationSize = 1)
	@GeneratedValue(generator = "cart_item_id",strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id" ,nullable = false)
	private User user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "product_id" ,nullable = false)
	private Product product;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@CreationTimestamp
	private LocalDateTime addedAt;
}
