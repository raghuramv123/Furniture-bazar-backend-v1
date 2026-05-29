package com.ram.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ram.demo.entity.CartItem;
import com.ram.demo.entity.Product;
import com.ram.demo.entity.User;
import com.ram.demo.exception.InsufficientStockException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.CartItemRepository;
import com.ram.demo.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public List<CartItem> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < quantity)
            throw new InsufficientStockException("Not enough stock");

        Optional<CartItem> existing = cartItemRepository
                .findByUserIdAndProductId(userId, productId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemRepository.save(item);
        }

        CartItem item = new CartItem();
        item.setUser(new User(userId)); // proxy reference
        item.setProduct(product);
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public CartItem updateQuantity(Long userId, Long productId, int quantity) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void removeItem(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(i -> {
                    BigDecimal price = i.getProduct().getSalePrice() != null
                            ? i.getProduct().getSalePrice()
                            : i.getProduct().getPrice();
                    return price.multiply(BigDecimal.valueOf(i.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}