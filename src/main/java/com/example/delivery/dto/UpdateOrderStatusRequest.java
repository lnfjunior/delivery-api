package com.example.delivery.dto;

import com.example.delivery.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
    @NotNull
    public OrderStatus status;
}
