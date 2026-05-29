package com.ram.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ram.demo.entity.Order;
import com.ram.demo.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // derived method — works as long as entity has 'orderNumber' field
    List<Order> findByUserIdOrderByOrderedAtDesc(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByStatus(OrderStatus status);

    // ── use @Query to avoid derived method parsing issues ──
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(
        @Param("userId") Long userId,
        @Param("status") OrderStatus status
    );

    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
           "WHERE o.status = :status AND o.orderedAt BETWEEN :from AND :to")
    BigDecimal totalRevenueBetween(
        @Param("status") OrderStatus status,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderedAt BETWEEN :from AND :to")
    long countOrdersBetween(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
}