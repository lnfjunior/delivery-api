package com.example.delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemRequest {
    @NotNull
    public UUID productId;
    @Min(1)
    public Integer quantity;
}
