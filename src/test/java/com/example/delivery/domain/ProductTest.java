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

@DisplayName("Product Entity Tests")
class ProductTest {

    private Validator validator;
    private Product product;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        product = new Product();
    }

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProductWithValidData() {
        UUID id = UUID.randomUUID();
        String name = "Test Product";
        BigDecimal price = new BigDecimal("29.99");

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        assertThat(product.getId()).isEqualTo(id);
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        product.setName("");
        product.setPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        product.setName(null);
        product.setPrice(new BigDecimal("29.99"));

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when price is null")
    void shouldFailValidationWhenPriceIsNull() {
        product.setName("Test Product");
        product.setPrice(null);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("price");
    }

    @Test
    @DisplayName("Should fail validation when price is negative")
    void shouldFailValidationWhenPriceIsNegative() {
        product.setName("Test Product");
        product.setPrice(new BigDecimal("-10.00"));

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("price");
    }

    @Test
    @DisplayName("Should allow zero price")
    void shouldAllowZeroPrice() {
        product.setName("Free Product");
        product.setPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
        assertThat(product.getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle large price values")
    void shouldHandleLargePriceValues() {
        product.setName("Expensive Product");
        BigDecimal largePrice = new BigDecimal("999999999999999.99");
        product.setPrice(largePrice);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
        assertThat(product.getPrice()).isEqualTo(largePrice);
    }

    @Test
    @DisplayName("Should handle price with many decimal places")
    void shouldHandlePriceWithManyDecimalPlaces() {
        product.setName("Precise Product");
        BigDecimal precisePrice = new BigDecimal("29.999999");
        product.setPrice(precisePrice);

        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        assertThat(violations).isEmpty();
        assertThat(product.getPrice()).isEqualTo(precisePrice);
    }
}

