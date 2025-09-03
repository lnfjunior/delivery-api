package com.example.delivery.dto;

import com.example.delivery.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    public UUID id;
    public OrderStatus status;
    public OffsetDateTime createdAt;
    public CustomerDto customer;
    public List<OrderItemResponse> items;
    public BigDecimal total;
}
