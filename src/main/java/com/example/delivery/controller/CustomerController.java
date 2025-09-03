package com.example.delivery.controller;

import com.example.delivery.dto.CreateCustomerRequest;
import com.example.delivery.dto.CustomerDto;
import com.example.delivery.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Operation(summary = "Create a customer")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','SCOPE_delivery.write')")
    public CustomerDto create(@Valid @RequestBody CreateCustomerRequest req) {
        return service.create(req);
    }

    @Operation(summary = "List customers")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public List<CustomerDto> list() {
        return service.list();
    }

    @Operation(summary = "Get customer by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public CustomerDto get(@PathVariable UUID id) {
        return service.get(id);
    }
}
