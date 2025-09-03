package com.example.delivery.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotFoundException Tests")
class NotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Resource not found";

        NotFoundException exception = new NotFoundException(message);

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should create exception with null message")
    void shouldCreateExceptionWithNullMessage() {
        NotFoundException exception = new NotFoundException(null);

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should create exception with empty message")
    void shouldCreateExceptionWithEmptyMessage() {
        String message = "";

        NotFoundException exception = new NotFoundException(message);

        assertThat(exception.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        String message = "Test exception";

        try {
            throw new NotFoundException(message);
        } catch (NotFoundException e) {
            assertThat(e.getMessage()).isEqualTo(message);
        }
    }
}

