package com.ram.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ram.demo.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByGatewayTransactionId(String txnId);
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
}