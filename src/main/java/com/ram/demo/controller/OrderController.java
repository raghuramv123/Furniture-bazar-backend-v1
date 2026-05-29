package com.ram.demo.controller;

import java.nio.file.AccessDeniedException;
import com.ram.demo.security.UserPrincipal;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.PlaceOrderRequest;
import com.ram.demo.entity.Order;
import com.ram.demo.security.UserPrincipal;
import com.ram.demo.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest req,
                                             Authentication auth) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(getUserId(auth), req));
    }

    @GetMapping
    public ResponseEntity<List<Order>> myOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getOrdersByUser(getUserId(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id, Authentication auth) throws AccessDeniedException {
        return ResponseEntity.ok(orderService.getOrderById(id, getUserId(auth)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication auth) throws AccessDeniedException, BadRequestException {
        orderService.cancelOrder(id, getUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication auth) {
        return ((UserPrincipal) auth.getPrincipal()).getId();
    }
}
