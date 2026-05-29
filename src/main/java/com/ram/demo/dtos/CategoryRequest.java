package com.ram.demo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$",
             message = "Slug must be lowercase letters, numbers, and hyphens only")
    private String slug;

    private Long    parentId;
    private String  imageUrl;      // ── was missing ──
    @Min(0)
    private Integer displayOrder = 0;
}