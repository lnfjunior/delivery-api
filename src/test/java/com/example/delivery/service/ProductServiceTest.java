package com.example.delivery.service;

import com.example.delivery.domain.Product;
import com.example.delivery.dto.CreateProductRequest;
import com.example.delivery.dto.ProductDto;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private CreateProductRequest createRequest;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        
        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));

        createRequest = new CreateProductRequest();
        createRequest.name = "Test Product";
        createRequest.price = new BigDecimal("29.99");
    }

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.create(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(productId);
        assertThat(result.name).isEqualTo("Test Product");
        assertThat(result.price).isEqualTo(new BigDecimal("29.99"));

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with zero price")
    void shouldCreateProductWithZeroPrice() {
        createRequest.price = BigDecimal.ZERO;
        product.setPrice(BigDecimal.ZERO);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.create(createRequest);

        assertThat(result.price).isEqualTo(BigDecimal.ZERO);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should create product with large price")
    void shouldCreateProductWithLargePrice() {
        BigDecimal largePrice = new BigDecimal("999999999999999.99");
        createRequest.price = largePrice;
        product.setPrice(largePrice);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.create(createRequest);

        assertThat(result.price).isEqualTo(largePrice);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should list all products")
    void shouldListAllProducts() {
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Another Product");
        product2.setPrice(new BigDecimal("19.99"));

        List<Product> products = Arrays.asList(product, product2);
        when(productRepository.findAll()).thenReturn(products);

        List<ProductDto> result = productService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name).isEqualTo("Test Product");
        assertThat(result.get(1).name).isEqualTo("Another Product");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void shouldReturnEmptyListWhenNoProductsExist() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<ProductDto> result = productService.list();

        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Should get product by id")
    void shouldGetProductById() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDto result = productService.get(productId);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(productId);
        assertThat(result.name).isEqualTo("Test Product");
        assertThat(result.price).isEqualTo(new BigDecimal("29.99"));

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found by id")
    void shouldThrowNotFoundExceptionWhenProductNotFoundById() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.get(productId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Product not found");

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should find product entity by id")
    void shouldFindProductEntityById() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product result = productService.findEntity(productId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("29.99"));

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when finding entity by non-existent id")
    void shouldThrowNotFoundExceptionWhenFindingEntityByNonExistentId() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findEntity(productId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Product not found");

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should convert product entity to DTO correctly")
    void shouldConvertProductEntityToDtoCorrectly() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDto result = productService.get(productId);

        assertThat(result.id).isEqualTo(product.getId());
        assertThat(result.name).isEqualTo(product.getName());
        assertThat(result.price).isEqualTo(product.getPrice());
    }

    @Test
    @DisplayName("Should handle product with precise decimal price")
    void shouldHandleProductWithPreciseDecimalPrice() {
        BigDecimal precisePrice = new BigDecimal("29.999999");
        createRequest.price = precisePrice;
        product.setPrice(precisePrice);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto result = productService.create(createRequest);

        assertThat(result.price).isEqualTo(precisePrice);
        verify(productRepository).save(any(Product.class));
    }
}

