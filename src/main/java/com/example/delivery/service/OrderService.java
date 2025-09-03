package com.example.delivery.service;

import com.example.delivery.domain.*;
import com.example.delivery.dto.*;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.productService = productService;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest req) {
        PurchaseOrder order = new PurchaseOrder();
        Customer customer = customerService.findEntity(req.customerId);
        order.setCustomer(customer);

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest ir : req.items) {
            Product p = productService.findEntity(ir.productId);
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(p);
            item.setQuantity(ir.quantity);
            item.setUnitPrice(p.getPrice());
            items.add(item);
        }
        order.setItems(items);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, UpdateOrderStatusRequest req) {
        PurchaseOrder order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(req.status);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(OrderStatus status) {
        List<PurchaseOrder> orders = (status == null) ? orderRepository.findAll() : orderRepository.findByStatus(status);
        return orders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse get(UUID id) {
        PurchaseOrder order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
        return toResponse(order);
    }

    private OrderResponse toResponse(PurchaseOrder order) {
        OrderResponse resp = new OrderResponse();
        resp.id = order.getId();
        resp.status = order.getStatus();
        resp.createdAt = order.getCreatedAt();
        resp.customer = customerService.get(order.getCustomer().getId());
        resp.items = order.getItems().stream().map(oi -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.productId = oi.getProduct().getId();
            ir.productName = oi.getProduct().getName();
            ir.unitPrice = oi.getUnitPrice();
            ir.quantity = oi.getQuantity();
            ir.total = oi.getUnitPrice().multiply(new java.math.BigDecimal(oi.getQuantity()));
            return ir;
        }).toList();
        resp.total = resp.items.stream().map(i -> i.total).reduce(BigDecimal.ZERO, BigDecimal::add);
        return resp;
    }
}
