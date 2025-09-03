package com.example.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class CreateProductRequest {
    @NotBlank
    public String name;
    @NotNull
    @PositiveOrZero
    public BigDecimal price;
}
