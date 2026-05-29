package com.ram.demo.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotNull(message = "Address ID is required")
    private Long addressId;

    // optional — null means no coupon applied
    private String couponCode;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
