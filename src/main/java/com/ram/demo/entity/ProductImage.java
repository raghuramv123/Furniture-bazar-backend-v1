package com.ram.demo.entity;

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
@Table(name="product_images")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ProductImage 
{

	@Id
	@SequenceGenerator(name="product_image_id",initialValue = 1,allocationSize = 1)
	@GeneratedValue(generator = "product_image_id",strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id",nullable = false)
	private Product product;
	
	@Column(nullable = false)
	private String url;
	
	private String altText;
	
	private Boolean isPrimary=false;
	private Integer sortOrder=0;
}
