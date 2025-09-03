package com.example.delivery.service;

import com.example.delivery.domain.Customer;
import com.example.delivery.dto.CreateCustomerRequest;
import com.example.delivery.dto.CustomerDto;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CreateCustomerRequest createRequest;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        
        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");

        createRequest = new CreateCustomerRequest();
        createRequest.name = "John Doe";
        createRequest.email = "john.doe@example.com";
        createRequest.phone = "+1234567890";
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomerSuccessfully() {
        when(customerRepository.existsByEmail(createRequest.email)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.create(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(customerId);
        assertThat(result.name).isEqualTo("John Doe");
        assertThat(result.email).isEqualTo("john.doe@example.com");
        assertThat(result.phone).isEqualTo("+1234567890");

        verify(customerRepository).existsByEmail(createRequest.email);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(customerRepository.existsByEmail(createRequest.email)).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(createRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Email already registered");

        verify(customerRepository).existsByEmail(createRequest.email);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should create customer with null phone")
    void shouldCreateCustomerWithNullPhone() {
        createRequest.phone = null;
        customer.setPhone(null);
        
        when(customerRepository.existsByEmail(createRequest.email)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.create(createRequest);

        assertThat(result.phone).isNull();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should list all customers")
    void shouldListAllCustomers() {
        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());
        customer2.setName("Jane Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setPhone("+0987654321");

        List<Customer> customers = Arrays.asList(customer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerDto> result = customerService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name).isEqualTo("John Doe");
        assertThat(result.get(1).name).isEqualTo("Jane Smith");

        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        when(customerRepository.findAll()).thenReturn(Arrays.asList());

        List<CustomerDto> result = customerService.list();

        assertThat(result).isEmpty();
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should get customer by id")
    void shouldGetCustomerById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.get(customerId);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(customerId);
        assertThat(result.name).isEqualTo("John Doe");
        assertThat(result.email).isEqualTo("john.doe@example.com");
        assertThat(result.phone).isEqualTo("+1234567890");

        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found by id")
    void shouldThrowNotFoundExceptionWhenCustomerNotFoundById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.get(customerId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Customer not found");

        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should find customer entity by id")
    void shouldFindCustomerEntityById() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer result = customerService.findEntity(customerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getPhone()).isEqualTo("+1234567890");

        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when finding entity by non-existent id")
    void shouldThrowNotFoundExceptionWhenFindingEntityByNonExistentId() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findEntity(customerId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Customer not found");

        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("Should handle customer with empty phone")
    void shouldHandleCustomerWithEmptyPhone() {
        createRequest.phone = "";
        customer.setPhone("");
        
        when(customerRepository.existsByEmail(createRequest.email)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.create(createRequest);

        assertThat(result.phone).isEmpty();
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should convert customer entity to DTO correctly")
    void shouldConvertCustomerEntityToDtoCorrectly() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.get(customerId);

        assertThat(result.id).isEqualTo(customer.getId());
        assertThat(result.name).isEqualTo(customer.getName());
        assertThat(result.email).isEqualTo(customer.getEmail());
        assertThat(result.phone).isEqualTo(customer.getPhone());
    }
}

