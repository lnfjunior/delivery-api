package com.example.delivery.controller;

import com.example.delivery.domain.OrderStatus;
import com.example.delivery.dto.CreateOrderRequest;
import com.example.delivery.dto.OrderResponse;
import com.example.delivery.dto.UpdateOrderStatusRequest;
import com.example.delivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Create an order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','SCOPE_delivery.write')")
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','SCOPE_delivery.write')")
    public OrderResponse updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        return service.updateStatus(id, req);
    }

    @Operation(summary = "List orders (filter by status)")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public List<OrderResponse> list(@RequestParam(value = "status", required = false) OrderStatus status) {
        return service.list(status);
    }

    @Operation(summary = "Get order by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public OrderResponse get(@PathVariable UUID id) {
        return service.get(id);
    }
}
