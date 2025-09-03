package com.example.delivery.repository;

import com.example.delivery.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ProductRepository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should save and find product by id")
    void shouldSaveAndFindProductById() {
        Product savedProduct = productRepository.save(product);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
        assertThat(foundProduct.get().getPrice()).isEqualTo(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should generate UUID for new product")
    void shouldGenerateUuidForNewProduct() {
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAllProducts() {
        Product product2 = new Product();
        product2.setName("Another Product");
        product2.setPrice(new BigDecimal("19.99"));

        productRepository.save(product);
        productRepository.save(product2);

        List<Product> products = productRepository.findAll();

        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
            .containsExactlyInAnyOrder("Test Product", "Another Product");
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        Product savedProduct = productRepository.save(product);

        productRepository.delete(savedProduct);

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        Product savedProduct = productRepository.save(product);

        savedProduct.setName("Updated Product");
        savedProduct.setPrice(new BigDecimal("39.99"));
        Product updatedProduct = productRepository.save(savedProduct);

        assertThat(updatedProduct.getId()).isEqualTo(savedProduct.getId());
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("39.99"));
    }

    @Test
    @DisplayName("Should count products")
    void shouldCountProducts() {
        Product product2 = new Product();
        product2.setName("Another Product");
        product2.setPrice(new BigDecimal("19.99"));

        productRepository.save(product);
        productRepository.save(product2);

        long count = productRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle product with zero price")
    void shouldHandleProductWithZeroPrice() {
        product.setPrice(BigDecimal.ZERO);

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.ZERO);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle product with large price")
    void shouldHandleProductWithLargePrice() {
        BigDecimal largePrice = new BigDecimal("999999999999999.99");
        product.setPrice(largePrice);

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getPrice()).isEqualTo(largePrice);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getPrice()).isEqualTo(largePrice);
    }

    @Test
    @DisplayName("Should handle product with precise decimal price")
    void shouldHandleProductWithPreciseDecimalPrice() {
        BigDecimal precisePrice = new BigDecimal("29.999999");
        product.setPrice(precisePrice);

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getPrice()).isEqualTo(precisePrice);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getPrice()).isEqualTo(precisePrice);
    }

    @Test
    @DisplayName("Should return empty optional for non-existent product")
    void shouldReturnEmptyOptionalForNonExistentProduct() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<Product> foundProduct = productRepository.findById(nonExistentId);

        assertThat(foundProduct).isEmpty();
    }
}

