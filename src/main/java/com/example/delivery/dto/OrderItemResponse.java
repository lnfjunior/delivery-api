package com.example.delivery.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {
    public UUID productId;
    public String productName;
    public BigDecimal unitPrice;
    public Integer quantity;
    public BigDecimal total;
}
