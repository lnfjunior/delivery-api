package com.example.delivery.controller;

import com.example.delivery.dto.CreateProductRequest;
import com.example.delivery.dto.ProductDto;
import com.example.delivery.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Create a product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','SCOPE_delivery.write')")
    public ProductDto create(@Valid @RequestBody CreateProductRequest req) {
        return service.create(req);
    }

    @Operation(summary = "List products")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public List<ProductDto> list() {
        return service.list();
    }

    @Operation(summary = "Get product by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_delivery_admin','ROLE_delivery_user','SCOPE_delivery','SCOPE_delivery.read')")
    public ProductDto get(@PathVariable UUID id) {
        return service.get(id);
    }
}
