package com.example.delivery.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ApiError Tests")
class ApiErrorTest {

    private ApiError apiError;

    @BeforeEach
    void setUp() {
        apiError = new ApiError();
    }

    @Test
    @DisplayName("Should initialize with current timestamp")
    void shouldInitializeWithCurrentTimestamp() {
        OffsetDateTime before = OffsetDateTime.now().minusSeconds(1);
        
        ApiError error = new ApiError();
        
        OffsetDateTime after = OffsetDateTime.now().plusSeconds(1);
        assertThat(error.timestamp).isNotNull();
        assertThat(error.timestamp).isAfter(before);
        assertThat(error.timestamp).isBefore(after);
    }

    @Test
    @DisplayName("Should set and get path")
    void shouldSetAndGetPath() {
        String path = "/api/v1/customers";

        apiError.path = path;

        assertThat(apiError.path).isEqualTo(path);
    }

    @Test
    @DisplayName("Should set and get status")
    void shouldSetAndGetStatus() {
        int status = 404;

        apiError.status = status;

        assertThat(apiError.status).isEqualTo(status);
    }

    @Test
    @DisplayName("Should set and get error")
    void shouldSetAndGetError() {
        String error = "Not Found";

        apiError.error = error;

        assertThat(apiError.error).isEqualTo(error);
    }

    @Test
    @DisplayName("Should set and get message")
    void shouldSetAndGetMessage() {
        String message = "Customer not found";

        apiError.message = message;

        assertThat(apiError.message).isEqualTo(message);
    }

    @Test
    @DisplayName("Should set and get timestamp")
    void shouldSetAndGetTimestamp() {
        OffsetDateTime timestamp = OffsetDateTime.now().minusHours(1);

        apiError.timestamp = timestamp;

        assertThat(apiError.timestamp).isEqualTo(timestamp);
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        apiError.path = null;
        apiError.error = null;
        apiError.message = null;
        apiError.timestamp = null;

        assertThat(apiError.path).isNull();
        assertThat(apiError.error).isNull();
        assertThat(apiError.message).isNull();
        assertThat(apiError.timestamp).isNull();
    }

    @Test
    @DisplayName("Should handle empty strings")
    void shouldHandleEmptyStrings() {
        apiError.path = "";
        apiError.error = "";
        apiError.message = "";

        assertThat(apiError.path).isEmpty();
        assertThat(apiError.error).isEmpty();
        assertThat(apiError.message).isEmpty();
    }

    @Test
    @DisplayName("Should set all fields correctly")
    void shouldSetAllFieldsCorrectly() {
        String path = "/api/v1/products/123";
        int status = 500;
        String error = "Internal Server Error";
        String message = "Database connection failed";
        OffsetDateTime timestamp = OffsetDateTime.now();

        apiError.path = path;
        apiError.status = status;
        apiError.error = error;
        apiError.message = message;
        apiError.timestamp = timestamp;

        assertThat(apiError.path).isEqualTo(path);
        assertThat(apiError.status).isEqualTo(status);
        assertThat(apiError.error).isEqualTo(error);
        assertThat(apiError.message).isEqualTo(message);
        assertThat(apiError.timestamp).isEqualTo(timestamp);
    }
}

