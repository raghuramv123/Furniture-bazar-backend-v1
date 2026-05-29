package com.ram.demo.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 50, message = "Quantity cannot exceed 50")
    private Integer quantity;
}
/*

// ── CartRequest.java ──────────────────────────────────────────────────


// ── PlaceOrderRequest.java ────────────────────────────────────────────

// ── ProductRequest.java ───────────────────────────────────────────────

// ── CategoryRequest.java ──────────────────────────────────────────────


// ── AddressRequest.java ───────────────────────────────────────────────


// ── ReviewRequest.java ────────────────────────────────────────────────


// ── PaymentVerifyRequest.java ─────────────────────────────────────────

 */