package com.example.delivery.repository;

import com.example.delivery.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("OrderRepository Tests")
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    private Product product;
    private PurchaseOrder order;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setPhone("+1234567890");
        customer = customerRepository.save(customer);

        product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));
        product = productRepository.save(product);

        order = new PurchaseOrder();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Should save and find order by id")
    void shouldSaveAndFindOrderById() {
        PurchaseOrder savedOrder = orderRepository.save(order);
        Optional<PurchaseOrder> foundOrder = orderRepository.findById(savedOrder.getId());

        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getCustomer().getName()).isEqualTo("John Doe");
        assertThat(foundOrder.get().getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("Should generate UUID for new order")
    void shouldGenerateUuidForNewOrder() {
        PurchaseOrder savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should find orders by status")
    void shouldFindOrdersByStatus() {
        PurchaseOrder order2 = new PurchaseOrder();
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setCreatedAt(OffsetDateTime.now());

        PurchaseOrder order3 = new PurchaseOrder();
        order3.setCustomer(customer);
        order3.setStatus(OrderStatus.CREATED);
        order3.setCreatedAt(OffsetDateTime.now());

        orderRepository.save(order);
        orderRepository.save(order2);
        orderRepository.save(order3);

        List<PurchaseOrder> createdOrders = orderRepository.findByStatus(OrderStatus.CREATED);
        List<PurchaseOrder> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);

        assertThat(createdOrders).hasSize(2);
        assertThat(processingOrders).hasSize(1);
        assertThat(createdOrders).allMatch(o -> o.getStatus() == OrderStatus.CREATED);
        assertThat(processingOrders).allMatch(o -> o.getStatus() == OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should return empty list for non-existent status")
    void shouldReturnEmptyListForNonExistentStatus() {
        orderRepository.save(order);

        List<PurchaseOrder> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);

        assertThat(deliveredOrders).isEmpty();
    }

    @Test
    @DisplayName("Should find all orders")
    void shouldFindAllOrders() {
        PurchaseOrder order2 = new PurchaseOrder();
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setCreatedAt(OffsetDateTime.now());

        orderRepository.save(order);
        orderRepository.save(order2);

        List<PurchaseOrder> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(PurchaseOrder::getStatus)
            .containsExactlyInAnyOrder(OrderStatus.CREATED, OrderStatus.PROCESSING);
    }

    @Test
    @DisplayName("Should delete order")
    void shouldDeleteOrder() {
        PurchaseOrder savedOrder = orderRepository.save(order);

        orderRepository.delete(savedOrder);

        Optional<PurchaseOrder> foundOrder = orderRepository.findById(savedOrder.getId());
        assertThat(foundOrder).isEmpty();
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        PurchaseOrder savedOrder = orderRepository.save(order);

        savedOrder.setStatus(OrderStatus.SHIPPED);
        PurchaseOrder updatedOrder = orderRepository.save(savedOrder);

        assertThat(updatedOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("Should save order with items")
    void shouldSaveOrderWithItems() {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("29.99"));
        item.setOrder(order);
        
        order.getItems().add(item);

        PurchaseOrder savedOrder = orderRepository.save(order);

        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getItems().get(0).getProduct().getName()).isEqualTo("Test Product");
        assertThat(savedOrder.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(savedOrder.getItems().get(0).getUnitPrice()).isEqualTo(new BigDecimal("29.99"));
    }

    @Test
    @DisplayName("Should count orders")
    void shouldCountOrders() {
        PurchaseOrder order2 = new PurchaseOrder();
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setCreatedAt(OffsetDateTime.now());

        orderRepository.save(order);
        orderRepository.save(order2);

        long count = orderRepository.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle all order statuses in findByStatus")
    void shouldHandleAllOrderStatusesInFindByStatus() {
        for (OrderStatus status : OrderStatus.values()) {
            PurchaseOrder orderWithStatus = new PurchaseOrder();
            orderWithStatus.setCustomer(customer);
            orderWithStatus.setStatus(status);
            orderWithStatus.setCreatedAt(OffsetDateTime.now());
            orderRepository.save(orderWithStatus);
        }

        for (OrderStatus status : OrderStatus.values()) {
            List<PurchaseOrder> orders = orderRepository.findByStatus(status);
            assertThat(orders).hasSize(1);
            assertThat(orders.get(0).getStatus()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("Should return empty optional for non-existent order")
    void shouldReturnEmptyOptionalForNonExistentOrder() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<PurchaseOrder> foundOrder = orderRepository.findById(nonExistentId);

        assertThat(foundOrder).isEmpty();
    }
}

