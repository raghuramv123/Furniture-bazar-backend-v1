package com.ram.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ram.demo.entity.Product;
import com.ram.demo.entity.User;
import com.ram.demo.entity.WishList;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.ProductRepository;
import com.ram.demo.repository.WishlistRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public List<WishList> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    public boolean toggle(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return false; // removed
        }
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        WishList w = new WishList();
        w.setUser(new User(userId));
        w.setProduct(new Product(productId));
        wishlistRepository.save(w);
        return true; // added
    }
}