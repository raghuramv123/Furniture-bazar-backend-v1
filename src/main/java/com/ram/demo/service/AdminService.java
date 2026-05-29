package com.ram.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ram.demo.entity.Order;
import com.ram.demo.entity.User;
import com.ram.demo.enums.OrderStatus;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.OrderRepository;
import com.ram.demo.repository.ProductRepository;
import com.ram.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Map<String, Object> getDashboardStats() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProducts", productRepository.count());
        stats.put("ordersThisMonth", orderRepository.countOrdersBetween(startOfMonth, now));
        stats.put("revenueThisMonth",
        	    orderRepository.totalRevenueBetween(OrderStatus.DELIVERED, startOfMonth, now));
//       stats.put("lowStockProducts", productRepository.findLowStockProducts(5, true));
        stats.put("lowStockProducts",
        	    productRepository.findByStockQuantityLessThanEqualAndActiveTrue(5));
        return stats;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by("orderedAt").descending());
    }
}