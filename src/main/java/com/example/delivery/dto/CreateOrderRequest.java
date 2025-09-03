package com.example.delivery.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class CreateOrderRequest {
    @NotNull
    public UUID customerId;

    @Size(min = 1)
    public List<OrderItemRequest> items;
}
