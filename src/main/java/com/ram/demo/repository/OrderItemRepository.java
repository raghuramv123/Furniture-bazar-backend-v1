package com.ram.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ram.demo.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi.product.id, SUM(oi.quantity) AS total FROM OrderItem oi " +
           "GROUP BY oi.product.id ORDER BY total DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);
}