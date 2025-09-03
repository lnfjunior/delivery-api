package com.example.delivery.service;

import com.example.delivery.domain.Customer;
import com.example.delivery.dto.CreateCustomerRequest;
import com.example.delivery.dto.CustomerDto;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CacheEvict(value = {"customers", "customers:list"}, allEntries = true)
    public CustomerDto create(CreateCustomerRequest req) {
        if (repository.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        Customer c = new Customer();
        c.setName(req.name);
        c.setEmail(req.email);
        c.setPhone(req.phone);
        c = repository.save(c);
        return toDto(c);
    }

    @Cacheable(value = "customers:list")
    public List<CustomerDto> list() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Cacheable(value = "customers", key = "#id")
    public CustomerDto get(UUID id) {
        Customer c = repository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
        return toDto(c);
    }

    public Customer findEntity(UUID id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    private CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.id = c.getId();
        dto.name = c.getName();
        dto.email = c.getEmail();
        dto.phone = c.getPhone();
        return dto;
    }
}
