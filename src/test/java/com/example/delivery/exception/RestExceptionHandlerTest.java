package com.example.delivery.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestExceptionHandler Tests")
class RestExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RestExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/customers");
    }

    @Test
    @DisplayName("Should handle NotFoundException correctly")
    void shouldHandleNotFoundExceptionCorrectly() {
        NotFoundException exception = new NotFoundException("Customer not found");

        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status).isEqualTo(404);
        assertThat(response.getBody().error).isEqualTo("Not Found");
        assertThat(response.getBody().message).isEqualTo("Customer not found");
        assertThat(response.getBody().path).isEqualTo("/api/v1/customers");
        assertThat(response.getBody().timestamp).isNotNull();
    }

    @Test
    @DisplayName("Should handle NotFoundException with null message")
    void shouldHandleNotFoundExceptionWithNullMessage() {
        NotFoundException exception = new NotFoundException(null);

        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().message).isNull();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException correctly")
    void shouldHandleMethodArgumentNotValidExceptionCorrectly() {
        ObjectError objectError = new ObjectError("customer", "Name is required");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(objectError));

        ResponseEntity<ApiError> response = exceptionHandler.handleValidation(methodArgumentNotValidException, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status).isEqualTo(400);
        assertThat(response.getBody().error).isEqualTo("Validation Error");
        assertThat(response.getBody().message).isEqualTo("Name is required");
        assertThat(response.getBody().path).isEqualTo("/api/v1/customers");
        assertThat(response.getBody().timestamp).isNotNull();
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with no errors")
    void shouldHandleMethodArgumentNotValidExceptionWithNoErrors() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        ResponseEntity<ApiError> response = exceptionHandler.handleValidation(methodArgumentNotValidException, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message).isEqualTo("Invalid request");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple errors")
    void shouldHandleMethodArgumentNotValidExceptionWithMultipleErrors() {
        ObjectError error1 = new ObjectError("customer", "Name is required");
        ObjectError error2 = new ObjectError("customer", "Email is invalid");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(error1, error2));

        ResponseEntity<ApiError> response = exceptionHandler.handleValidation(methodArgumentNotValidException, request);

        assertThat(response.getBody().message).isEqualTo("Name is required");
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericExceptionCorrectly() {
        Exception exception = new RuntimeException("Database connection failed");

        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status).isEqualTo(500);
        assertThat(response.getBody().error).isEqualTo("Internal Server Error");
        assertThat(response.getBody().message).isEqualTo("Database connection failed");
        assertThat(response.getBody().path).isEqualTo("/api/v1/customers");
        assertThat(response.getBody().timestamp).isNotNull();
    }

    @Test
    @DisplayName("Should handle generic Exception with null message")
    void shouldHandleGenericExceptionWithNullMessage() {
        Exception exception = new RuntimeException((String) null);

        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message).isNull();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException as generic exception")
    void shouldHandleIllegalArgumentExceptionAsGenericException() {
        IllegalArgumentException exception = new IllegalArgumentException("Email already registered");

        ResponseEntity<ApiError> response = exceptionHandler.handleGeneric(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().message).isEqualTo("Email already registered");
    }

    @Test
    @DisplayName("Should set correct path from request URI")
    void shouldSetCorrectPathFromRequestUri() {
        when(request.getRequestURI()).thenReturn("/api/v1/products/123");
        NotFoundException exception = new NotFoundException("Product not found");

        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(exception, request);

        assertThat(response.getBody().path).isEqualTo("/api/v1/products/123");
    }
}

