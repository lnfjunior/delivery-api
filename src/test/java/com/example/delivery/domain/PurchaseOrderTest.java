package com.example.delivery.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PurchaseOrder Entity Tests")
class PurchaseOrderTest {

    private PurchaseOrder order;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        order = new PurchaseOrder();
        
        customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should create order with valid data")
    void shouldCreateOrderWithValidData() {
        UUID id = UUID.randomUUID();
        OffsetDateTime createdAt = OffsetDateTime.now();

        order.setId(id);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(createdAt);

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getCustomer()).isEqualTo(customer);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getCreatedAt()).isEqualTo(createdAt);
        assertThat(order.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should initialize with default status CREATED")
    void shouldInitializeWithDefaultStatusCreated() {
        PurchaseOrder newOrder = new PurchaseOrder();

        assertThat(newOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("Should initialize with empty items list")
    void shouldInitializeWithEmptyItemsList() {
        PurchaseOrder newOrder = new PurchaseOrder();

        assertThat(newOrder.getItems()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should allow setting different order statuses")
    void shouldAllowSettingDifferentOrderStatuses() {
        for (OrderStatus status : OrderStatus.values()) {
            order.setStatus(status);
            assertThat(order.getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should manage order items")
    void shouldManageOrderItems() {
        OrderItem item1 = new OrderItem();
        item1.setProduct(product);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("29.99"));
        item1.setOrder(order);

        OrderItem item2 = new OrderItem();
        item2.setProduct(product);
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("19.99"));
        item2.setOrder(order);

        List<OrderItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        order.setItems(items);

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItems()).contains(item1, item2);
    }

    @Test
    @DisplayName("Should handle null customer")
    void shouldHandleNullCustomer() {
        order.setCustomer(null);

        assertThat(order.getCustomer()).isNull();
    }

    @Test
    @DisplayName("Should handle null created date")
    void shouldHandleNullCreatedDate() {
        order.setCreatedAt(null);

        assertThat(order.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("Should replace items list when setting new items")
    void shouldReplaceItemsListWhenSettingNewItems() {
        OrderItem originalItem = new OrderItem();
        originalItem.setProduct(product);
        originalItem.setQuantity(1);
        originalItem.setUnitPrice(new BigDecimal("10.00"));

        List<OrderItem> originalItems = new ArrayList<>();
        originalItems.add(originalItem);
        order.setItems(originalItems);

        OrderItem newItem = new OrderItem();
        newItem.setProduct(product);
        newItem.setQuantity(3);
        newItem.setUnitPrice(new BigDecimal("15.00"));

        List<OrderItem> newItems = new ArrayList<>();
        newItems.add(newItem);

        order.setItems(newItems);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems()).contains(newItem);
        assertThat(order.getItems()).doesNotContain(originalItem);
    }
}

