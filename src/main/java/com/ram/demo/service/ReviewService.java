package com.ram.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ram.demo.dtos.ReviewRequest;
import com.ram.demo.entity.Product;
import com.ram.demo.entity.Review;
import com.ram.demo.entity.User;
import com.ram.demo.enums.OrderStatus;
import com.ram.demo.exception.DuplicateResourceException;
import com.ram.demo.exception.ResourceNotFoundException;
import com.ram.demo.repository.OrderRepository;
import com.ram.demo.repository.ProductRepository;
import com.ram.demo.repository.ReviewRepository;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository   = orderRepository;
    }

    public List<Review> getProductReviews(Long productId) {
        // ── FIXED: uses approvedTrue now ──
        return reviewRepository.findByProductIdAndApprovedTrue(productId);
    }

    public Double getAverageRating(Long productId) {
        return reviewRepository.averageRatingByProductId(productId);
    }

    public Review addReview(Long userId, Long productId, ReviewRequest req) {
        if (reviewRepository.existsByProductIdAndUserId(productId, userId))
            throw new DuplicateResourceException(
                "You have already reviewed this product");

        // check if user purchased and received this product
        boolean purchased = orderRepository
                .findByUserIdAndStatus(userId, OrderStatus.DELIVERED)
                .stream()
                .flatMap(o -> o.getOrderItems().stream())
                .anyMatch(oi -> oi.getProduct().getId().equals(productId));

        Product product = new Product();
        product.setId(productId);

        User user = new User();
        user.setId(userId);

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(req.getRating())
                .title(req.getTitle())
                .body(req.getBody())
                .verifiedPurchase(purchased)  // ── FIXED: was isVerifiedPurchase ──
                .approved(true)               // ── FIXED: was isApproved ──
                .build();

        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Review not found: " + reviewId));

        if (!review.getUser().getId().equals(userId))
            throw new AccessDeniedException("Not your review");

        reviewRepository.delete(review);
    }
}