package com.ram.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.ram.demo.enums.PaymentGateway;
import com.ram.demo.enums.PaymentMethod;
import com.ram.demo.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="payments")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Payment {

	@Id
	@SequenceGenerator(name="payments_id",initialValue = 1,allocationSize = 1)
	@GeneratedValue(generator = "payments_id",strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@OneToOne(fetch =  FetchType.LAZY)
	@JoinColumn(name="order_id",nullable = false)
	private Order order;
	
	@Enumerated(EnumType.STRING)
	private PaymentGateway gateway;
	
	
	private String gatewayTransactionId;
	private String gatewayOrderId;
	private String gatewaySignature;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	@Column(precision = 10,scale = 2)
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private PaymentMethod method;
	
	   @Column(precision = 10, scale = 2) private BigDecimal amount;
	private String failureReason;
	
	private LocalDateTime paidAt;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
}
