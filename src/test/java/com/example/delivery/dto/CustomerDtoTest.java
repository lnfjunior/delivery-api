package com.example.delivery.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomerDto Tests")
class CustomerDtoTest {

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
    }

    @Test
    @DisplayName("Should set and get id correctly")
    void shouldSetAndGetIdCorrectly() {
        UUID id = UUID.randomUUID();

        customerDto.id = id;

        assertThat(customerDto.id).isEqualTo(id);
    }

    @Test
    @DisplayName("Should set and get name correctly")
    void shouldSetAndGetNameCorrectly() {
        String name = "John Doe";

        customerDto.name = name;

        assertThat(customerDto.name).isEqualTo(name);
    }

    @Test
    @DisplayName("Should set and get email correctly")
    void shouldSetAndGetEmailCorrectly() {
        String email = "john.doe@example.com";

        customerDto.email = email;

        assertThat(customerDto.email).isEqualTo(email);
    }

    @Test
    @DisplayName("Should set and get phone correctly")
    void shouldSetAndGetPhoneCorrectly() {
        String phone = "+1234567890";

        customerDto.phone = phone;

        assertThat(customerDto.phone).isEqualTo(phone);
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        customerDto.id = null;
        customerDto.name = null;
        customerDto.email = null;
        customerDto.phone = null;

        assertThat(customerDto.id).isNull();
        assertThat(customerDto.name).isNull();
        assertThat(customerDto.email).isNull();
        assertThat(customerDto.phone).isNull();
    }

    @Test
    @DisplayName("Should handle empty strings")
    void shouldHandleEmptyStrings() {
        customerDto.name = "";
        customerDto.email = "";
        customerDto.phone = "";

        assertThat(customerDto.name).isEmpty();
        assertThat(customerDto.email).isEmpty();
        assertThat(customerDto.phone).isEmpty();
    }

    @Test
    @DisplayName("Should set all fields correctly")
    void shouldSetAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        String phone = "+0987654321";

        customerDto.id = id;
        customerDto.name = name;
        customerDto.email = email;
        customerDto.phone = phone;

        assertThat(customerDto.id).isEqualTo(id);
        assertThat(customerDto.name).isEqualTo(name);
        assertThat(customerDto.email).isEqualTo(email);
        assertThat(customerDto.phone).isEqualTo(phone);
    }

    @Test
    @DisplayName("Should create new instance with default values")
    void shouldCreateNewInstanceWithDefaultValues() {
        CustomerDto newDto = new CustomerDto();

        assertThat(newDto.id).isNull();
        assertThat(newDto.name).isNull();
        assertThat(newDto.email).isNull();
        assertThat(newDto.phone).isNull();
    }
}

