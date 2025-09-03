package com.example.delivery.controller;

import com.example.delivery.config.TestSecurityConfig;
import com.example.delivery.dto.CreateCustomerRequest;
import com.example.delivery.dto.CustomerDto;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(TestSecurityConfig.class)
@DisplayName("CustomerController Simple Tests")
class CustomerControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerDto customerDto;
    private CreateCustomerRequest createRequest;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        
        customerDto = new CustomerDto();
        customerDto.id = customerId;
        customerDto.name = "John Doe";
        customerDto.email = "john.doe@example.com";
        customerDto.phone = "+1234567890";

        createRequest = new CreateCustomerRequest();
        createRequest.name = "John Doe";
        createRequest.email = "john.doe@example.com";
        createRequest.phone = "+1234567890";
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() throws Exception {
        when(customerService.create(any(CreateCustomerRequest.class))).thenReturn(customerDto);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));

        verify(customerService).create(any(CreateCustomerRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when creating customer with invalid data")
    void shouldReturn400WhenCreatingCustomerWithInvalidData() throws Exception {
        createRequest.name = "";
        createRequest.email = "invalid-email";

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).create(any());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException when creating customer")
    void shouldHandleIllegalArgumentExceptionWhenCreatingCustomer() throws Exception {
        when(customerService.create(any(CreateCustomerRequest.class)))
            .thenThrow(new IllegalArgumentException("Email already registered"));

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isInternalServerError());

        verify(customerService).create(any(CreateCustomerRequest.class));
    }

    @Test
    @DisplayName("Should list customers successfully")
    void shouldListCustomersSuccessfully() throws Exception {
        CustomerDto customer2 = new CustomerDto();
        customer2.id = UUID.randomUUID();
        customer2.name = "Jane Smith";
        customer2.email = "jane.smith@example.com";

        List<CustomerDto> customers = Arrays.asList(customerDto, customer2);
        when(customerService.list()).thenReturn(customers);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(customerService).list();
    }

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() throws Exception {
        when(customerService.get(customerId)).thenReturn(customerDto);

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));

        verify(customerService).get(customerId);
    }

    @Test
    @DisplayName("Should return 404 when getting non-existent customer")
    void shouldReturn404WhenGettingNonExistentCustomer() throws Exception {
        when(customerService.get(customerId)).thenThrow(new NotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/{id}", customerId))
                .andExpect(status().isNotFound());

        verify(customerService).get(customerId);
    }

    @Test
    @DisplayName("Should create customer with null phone")
    void shouldCreateCustomerWithNullPhone() throws Exception {
        createRequest.phone = null;
        customerDto.phone = null;
        when(customerService.create(any(CreateCustomerRequest.class))).thenReturn(customerDto);

        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phone").isEmpty());

        verify(customerService).create(any(CreateCustomerRequest.class));
    }
}

