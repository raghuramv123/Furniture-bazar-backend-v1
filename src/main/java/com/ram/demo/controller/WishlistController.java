package com.ram.demo.controller;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.entity.WishList;
import com.ram.demo.security.UserPrincipal;
import com.ram.demo.service.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishList>> get(Authentication auth) {
        return ResponseEntity.ok(wishlistService.getWishlist(getUserId(auth)));
    }

    @PostMapping("/{productId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggle(@PathVariable Long productId,
                                                        Authentication auth) {
        boolean added = wishlistService.toggle(getUserId(auth), productId);
        return ResponseEntity.ok(Map.of("added", added));
    }


    private Long getUserId(Authentication auth) {
        return ((UserPrincipal) auth.getPrincipal()).getId();
    }
}
