package com.example.delivery.service;

import com.example.delivery.domain.Product;
import com.example.delivery.dto.CreateProductRequest;
import com.example.delivery.dto.ProductDto;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CacheEvict(value = {"products", "products:list"}, allEntries = true)
    public ProductDto create(CreateProductRequest req) {
        Product p = new Product();
        p.setName(req.name);
        p.setPrice(req.price);
        p = repository.save(p);
        return toDto(p);
    }

    @Cacheable(value = "products:list")
    public List<ProductDto> list() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Cacheable(value = "products", key = "#id")
    public ProductDto get(UUID id) {
        Product p = repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return toDto(p);
    }

    public Product findEntity(UUID id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.id = p.getId();
        dto.name = p.getName();
        dto.price = p.getPrice();
        return dto;
    }
}
