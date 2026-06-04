package com.ram.demo.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "Sale price must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal salePrice; // optional

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "SKU is required")
    @Size(max = 100)
    private String sku;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Furniture-specific — all optional
    @Size(max = 100) private String material;
    @Size(max = 100) private String dimensions;
    @Digits(integer = 5, fraction = 2) private BigDecimal weightKg;
    @Size(max = 50)  private String color;
    @Size(max = 50)  private String finish;
    @Size(max = 100) private String installationType;
    @Size(max = 100) private String frameType;
    @Size(max = 100) private String glassType;
    @Size(max = 50)  private String woodGrade;
    @Min(0)          private Integer thickness;

 // ── FIXED: Boolean (wrapper) not boolean (primitive) ──
 // primitive boolean defaults to false if JSON sends null
 // wrapper Boolean can be null and we handle it explicitly

 private Boolean active;
 private Boolean featured;
}
