package com.example.delivery.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Customer Entity Tests")
class CustomerTest {

    private Validator validator;
    private Customer customer;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should create customer with valid data")
    void shouldCreateCustomerWithValidData() {
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john.doe@example.com";
        String phone = "+1234567890";

        customer.setId(id);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getPhone()).isEqualTo(phone);

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        customer.setName("");
        customer.setEmail("john.doe@example.com");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        customer.setName(null);
        customer.setEmail("john.doe@example.com");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        customer.setName("John Doe");
        customer.setEmail("");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        customer.setName("John Doe");
        customer.setEmail(null);

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should fail validation when email format is invalid")
    void shouldFailValidationWhenEmailFormatIsInvalid() {
        customer.setName("John Doe");
        customer.setEmail("invalid-email");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should allow null phone")
    void shouldAllowNullPhone() {
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone(null);

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).isEmpty();
        assertThat(customer.getPhone()).isNull();
    }

    @Test
    @DisplayName("Should allow empty phone")
    void shouldAllowEmptyPhone() {
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("");

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        assertThat(violations).isEmpty();
        assertThat(customer.getPhone()).isEmpty();
    }
}

