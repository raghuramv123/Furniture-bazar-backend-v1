package com.ram.demo.controller;

import java.math.BigDecimal;
import com.ram.demo.security.UserPrincipal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.CartRequest;
import com.ram.demo.entity.CartItem;
import com.ram.demo.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private Long currentUserId() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        // resolved via UserDetailsService — you can inject UserRepository here
        // or use a helper bean; kept concise for clarity
        return ((UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart() {
        return ResponseEntity.ok(cartService.getCart(currentUserId()));
    }

    @PostMapping
    public ResponseEntity<CartItem> add(@Valid @RequestBody CartRequest req) {
        return ResponseEntity.ok(
                cartService.addToCart(currentUserId(), req.getProductId(), req.getQuantity()));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<CartItem> update(@PathVariable Long productId,
                                            @RequestParam int quantity) {
        return ResponseEntity.ok(
                cartService.updateQuantity(currentUserId(), productId, quantity));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId) {
        cartService.removeItem(currentUserId(), productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear() {
        cartService.clearCart(currentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> total() {
        return ResponseEntity.ok(Map.of("total", cartService.getCartTotal(currentUserId())));
    }
}