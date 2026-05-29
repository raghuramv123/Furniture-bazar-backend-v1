package com.ram.demo.service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.ram.demo.dtos.PlaceOrderRequest;
import com.ram.demo.entity.Address;
import com.ram.demo.entity.CartItem;
import com.ram.demo.entity.Coupon;
import com.ram.demo.entity.Order;
import com.ram.demo.entity.OrderItem;
import com.ram.demo.entity.Product;
import com.ram.demo.entity.User;
import com.ram.demo.enums.DiscountType;
import com.ram.demo.enums.OrderStatus;
import com.ram.demo.exception.InsufficientStockException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.AddressRepository;
import com.ram.demo.repository.CartItemRepository;
import com.ram.demo.repository.CouponRepository;
import com.ram.demo.repository.OrderItemRepository;
import com.ram.demo.repository.OrderRepository;
import com.ram.demo.repository.ProductRepository;
import com.ram.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public Order placeOrder(Long userId, PlaceOrderRequest req) throws BadRequestException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) throw new BadRequestException("Cart is empty");

        // Build order
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);

        // Build line items and deduct stock
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            if (p.getStockQuantity() < ci.getQuantity())
                throw new InsufficientStockException("Insufficient stock for: " + p.getName());

            p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
            productRepository.save(p);

            BigDecimal unitPrice = p.getSalePrice() != null ? p.getSalePrice() : p.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity()));

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(unitPrice);
            oi.setTotalPrice(lineTotal);
            oi.setProductSnapshot(buildSnapshot(p));
            items.add(oi);
            subtotal = subtotal.add(lineTotal);
        }

        // Apply coupon if any
        BigDecimal discount = BigDecimal.ZERO;
        if (req.getCouponCode() != null) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(req.getCouponCode())
                    .orElseThrow(() -> new BadRequestException("Invalid coupon code"));
            discount = applyCoupon(coupon, subtotal);
            order.setCoupon(coupon);
            coupon.setUsedCount(coupon.getUsedCount() + 1);
        }

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18")); // 18% GST
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("5000")) >= 0
                ? BigDecimal.ZERO : new BigDecimal("299"); // free above ₹5000

        order.setSubtotal(subtotal);
        order.setTaxAmount(tax);
        order.setShippingCost(shipping);
        order.setDiscountAmount(discount);
        order.setTotalAmount(subtotal.add(tax).add(shipping).subtract(discount));
        order.setNotes(req.getNotes());

        Order saved = orderRepository.save(order);
        items.forEach(i -> { i.setOrder(saved); orderItemRepository.save(i); });

        cartItemRepository.deleteByUserId(userId); // clear cart after order
        return saved;
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderedAtDesc(userId);
    }

    public Order getOrderById(Long orderId, Long userId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId))
            throw new AccessDeniedException("Not your order");
        return order;
    }

    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId, Long userId) throws AccessDeniedException, BadRequestException {
        Order order = getOrderById(orderId, userId);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED)
            throw new BadRequestException("Order cannot be cancelled at this stage");

        // Restore stock
        order.getOrderItems().forEach(oi -> {
            Product p = oi.getProduct();
            p.setStockQuantity(p.getStockQuantity() + oi.getQuantity());
            productRepository.save(p);
        });

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private BigDecimal applyCoupon(Coupon coupon, BigDecimal subtotal) throws BadRequestException {
        if (subtotal.compareTo(coupon.getMinOrderValue()) < 0)
            throw new BadRequestException("Minimum order value for this coupon is ₹"
                    + coupon.getMinOrderValue());

        BigDecimal discount = coupon.getDiscountType() == DiscountType.PERCENTAGE
                ? subtotal.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"))
                : coupon.getDiscountValue();

        if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0)
            discount = coupon.getMaxDiscount();

        return discount;
    }

    private String buildSnapshot(Product p) {
        return String.format("{\"name\":\"%s\",\"sku\":\"%s\",\"dimensions\":\"%s\"}",
                p.getName(), p.getSku(), p.getDimensions());
    }

    private String generateOrderNumber() {
        return "ORD-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%05d", (int)(Math.random() * 99999));
    }
}	