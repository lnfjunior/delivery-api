package com.example.delivery.repository;

import com.example.delivery.domain.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("CustomerRepository Tests")
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");
    }

    @Test
    @DisplayName("Should save and find customer by id")
    void shouldSaveAndFindCustomerById() {
        Customer savedCustomer = customerRepository.save(customer);
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getName()).isEqualTo("John Doe");
        assertThat(foundCustomer.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(foundCustomer.get().getPhone()).isEqualTo("+1234567890");
    }

    @Test
    @DisplayName("Should generate UUID for new customer")
    void shouldGenerateUuidForNewCustomer() {
        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should find all customers")
    void shouldFindAllCustomers() {
        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setPhone("+0987654321");

        customerRepository.save(customer);
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(Customer::getName)
            .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    @DisplayName("Should delete customer")
    void shouldDeleteCustomer() {
        Customer savedCustomer = customerRepository.save(customer);

        customerRepository.delete(savedCustomer);

        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isEmpty();
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void shouldCheckIfCustomerExistsByEmail() {
        customerRepository.save(customer);

        boolean exists = customerRepository.existsByEmail("john.doe@example.com");
        boolean notExists = customerRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should return false for null email in existsByEmail")
    void shouldReturnFalseForNullEmailInExistsByEmail() {
        boolean exists = customerRepository.existsByEmail(null);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return false for empty email in existsByEmail")
    void shouldReturnFalseForEmptyEmailInExistsByEmail() {
        boolean exists = customerRepository.existsByEmail("");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should update customer")
    void shouldUpdateCustomer() {
        Customer savedCustomer = customerRepository.save(customer);

        savedCustomer.setName("John Updated");
        savedCustomer.setEmail("john.updated@example.com");
        Customer updatedCustomer = customerRepository.save(savedCustomer);

        assertThat(updatedCustomer.getId()).isEqualTo(savedCustomer.getId());
        assertThat(updatedCustomer.getName()).isEqualTo("John Updated");
        assertThat(updatedCustomer.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    @DisplayName("Should count customers")
    void shouldCountCustomers() {
        Customer customer2 = new Customer();
        customer2.setName("Jane Smith");
        customer2.setEmail("jane.smith@example.com");

        customerRepository.save(customer);
        customerRepository.save(customer2);

        long count = customerRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle customer with null phone")
    void shouldHandleCustomerWithNullPhone() {
        customer.setPhone(null);

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer.getPhone()).isNull();
        Optional<Customer> foundCustomer = customerRepository.findById(savedCustomer.getId());
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getPhone()).isNull();
    }
}

