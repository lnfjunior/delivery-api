package com.example.delivery.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateCustomerRequest DTO Tests")
class CreateCustomerRequestTest {

    private Validator validator;
    private CreateCustomerRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new CreateCustomerRequest();
    }

    @Test
    @DisplayName("Should validate successfully with valid data")
    void shouldValidateSuccessfullyWithValidData() {
        request.name = "John Doe";
        request.email = "john.doe@example.com";
        request.phone = "+1234567890";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void shouldFailValidationWhenNameIsBlank() {
        request.name = "";
        request.email = "john.doe@example.com";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        request.name = null;
        request.email = "john.doe@example.com";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when name is only whitespace")
    void shouldFailValidationWhenNameIsOnlyWhitespace() {
        request.name = "   ";
        request.email = "john.doe@example.com";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("name");
    }

    @Test
    @DisplayName("Should fail validation when email is blank")
    void shouldFailValidationWhenEmailIsBlank() {
        request.name = "John Doe";
        request.email = "";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        request.name = "John Doe";
        request.email = null;

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should fail validation when email format is invalid")
    void shouldFailValidationWhenEmailFormatIsInvalid() {
        request.name = "John Doe";
        request.email = "invalid-email";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("Should fail validation when email is only whitespace")
    void shouldFailValidationWhenEmailIsOnlyWhitespace() {
        request.name = "John Doe";
        request.email = "   ";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
    }

    @Test
    @DisplayName("Should allow null phone")
    void shouldAllowNullPhone() {
        request.name = "John Doe";
        request.email = "john.doe@example.com";
        request.phone = null;

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should allow empty phone")
    void shouldAllowEmptyPhone() {
        request.name = "John Doe";
        request.email = "john.doe@example.com";
        request.phone = "";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should allow phone with various formats")
    void shouldAllowPhoneWithVariousFormats() {
        request.name = "John Doe";
        request.email = "john.doe@example.com";

        String[] phoneFormats = {
            "+1234567890",
            "(123) 456-7890",
            "123-456-7890",
            "123.456.7890",
            "1234567890"
        };

        for (String phone : phoneFormats) {
            request.phone = phone;
            Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("Should handle multiple validation errors")
    void shouldHandleMultipleValidationErrors() {
        request.name = "";
        request.email = "invalid-email";

        Set<ConstraintViolation<CreateCustomerRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
        assertThat(violations).extracting(v -> v.getPropertyPath().toString())
            .containsExactlyInAnyOrder("name", "email");
    }

    @Test
    @DisplayName("Should set and get all fields correctly")
    void shouldSetAndGetAllFieldsCorrectly() {
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        String phone = "+0987654321";

        request.name = name;
        request.email = email;
        request.phone = phone;

        assertThat(request.name).isEqualTo(name);
        assertThat(request.email).isEqualTo(email);
        assertThat(request.phone).isEqualTo(phone);
    }
}

