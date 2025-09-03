package com.example.delivery.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    private Validator validator;
    private OrderItem orderItem;
    private PurchaseOrder order;
    private Product product;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        orderItem = new OrderItem();

        order = new PurchaseOrder();
        order.setId(UUID.randomUUID());

        product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should create order item with valid data")
    void shouldCreateOrderItemWithValidData() {
        Long id = 1L;
        Integer quantity = 2;
        BigDecimal unitPrice = new BigDecimal("29.99");

        orderItem.setId(id);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);

        assertThat(orderItem.getId()).isEqualTo(id);
        assertThat(orderItem.getOrder()).isEqualTo(order);
        assertThat(orderItem.getProduct()).isEqualTo(product);
        assertThat(orderItem.getQuantity()).isEqualTo(quantity);
        assertThat(orderItem.getUnitPrice()).isEqualTo(unitPrice);

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when quantity is zero")
    void shouldFailValidationWhenQuantityIsZero() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(0);
        orderItem.setUnitPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("quantity");
    }

    @Test
    @DisplayName("Should fail validation when quantity is negative")
    void shouldFailValidationWhenQuantityIsNegative() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(-1);
        orderItem.setUnitPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("quantity");
    }

    @Test
    @DisplayName("Should allow minimum valid quantity of 1")
    void shouldAllowMinimumValidQuantityOfOne() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).isEmpty();
        assertThat(orderItem.getQuantity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should allow large quantities")
    void shouldAllowLargeQuantities() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1000);
        orderItem.setUnitPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).isEmpty();
        assertThat(orderItem.getQuantity()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Should handle null order")
    void shouldHandleNullOrder() {
        orderItem.setOrder(null);

        assertThat(orderItem.getOrder()).isNull();
    }

    @Test
    @DisplayName("Should handle null product")
    void shouldHandleNullProduct() {
        orderItem.setProduct(null);

        assertThat(orderItem.getProduct()).isNull();
    }

    @Test
    @DisplayName("Should handle null quantity")
    void shouldHandleNullQuantity() {
        orderItem.setQuantity(null);

        assertThat(orderItem.getQuantity()).isNull();
    }

    @Test
    @DisplayName("Should handle null unit price")
    void shouldHandleNullUnitPrice() {
        orderItem.setUnitPrice(null);

        assertThat(orderItem.getUnitPrice()).isNull();
    }

    @Test
    @DisplayName("Should handle zero unit price")
    void shouldHandleZeroUnitPrice() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).isEmpty();
        assertThat(orderItem.getUnitPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle negative unit price")
    void shouldHandleNegativeUnitPrice() {
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("-10.00"));

        Set<ConstraintViolation<OrderItem>> violations = validator.validate(orderItem);

        assertThat(violations).isEmpty();
        assertThat(orderItem.getUnitPrice()).isEqualTo(new BigDecimal("-10.00"));
    }
}

