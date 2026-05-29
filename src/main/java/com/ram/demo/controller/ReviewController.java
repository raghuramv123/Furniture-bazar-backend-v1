package com.ram.demo.controller;

import com.ram.demo.security.UserPrincipal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ram.demo.dtos.ReviewRequest;
import com.ram.demo.entity.Review;
import com.ram.demo.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Map<String, Double>> getAvgRating(@PathVariable Long productId) {
        return ResponseEntity.ok(
                Map.of("averageRating", reviewService.getAverageRating(productId)));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<Review> addReview(@PathVariable Long productId,
                                             @Valid @RequestBody ReviewRequest req,
                                             Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.addReview(getUserId(auth), productId, req));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, Authentication auth) {
        reviewService.deleteReview(reviewId, getUserId(auth));
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Authentication auth) {
        return ((UserPrincipal) auth.getPrincipal()).getId();
    }
}
