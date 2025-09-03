package com.example.delivery.service;

import com.example.delivery.domain.*;
import com.example.delivery.dto.*;
import com.example.delivery.exception.NotFoundException;
import com.example.delivery.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;
    private PurchaseOrder order;
    private CreateOrderRequest createRequest;
    private UpdateOrderStatusRequest updateStatusRequest;
    private UUID orderId;
    private UUID customerId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("29.99"));

        order = new PurchaseOrder();
        order.setId(orderId);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(OffsetDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setUnitPrice(new BigDecimal("29.99"));
        order.getItems().add(orderItem);

        createRequest = new CreateOrderRequest();
        createRequest.customerId = customerId;
        
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.productId = productId;
        itemRequest.quantity = 2;
        createRequest.items = Arrays.asList(itemRequest);

        updateStatusRequest = new UpdateOrderStatusRequest();
        updateStatusRequest.status = OrderStatus.PROCESSING;
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        when(customerService.findEntity(customerId)).thenReturn(customer);
        when(productService.findEntity(productId)).thenReturn(product);
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(order);
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        customerDto.name = "John Doe";
        customerDto.email = "john.doe@example.com";
        when(customerService.get(customerId)).thenReturn(customerDto);

        OrderResponse result = orderService.create(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(orderId);
        assertThat(result.status).isEqualTo(OrderStatus.CREATED);
        assertThat(result.customer.id).isEqualTo(customerId);
        assertThat(result.items).hasSize(1);
        assertThat(result.items.get(0).productId).isEqualTo(productId);
        assertThat(result.items.get(0).quantity).isEqualTo(2);
        assertThat(result.total).isEqualTo(new BigDecimal("59.98"));

        verify(customerService).findEntity(customerId);
        verify(productService).findEntity(productId);
        verify(orderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found during order creation")
    void shouldThrowNotFoundExceptionWhenCustomerNotFoundDuringOrderCreation() {
        when(customerService.findEntity(customerId)).thenThrow(new NotFoundException("Customer not found"));

        assertThatThrownBy(() -> orderService.create(createRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Customer not found");

        verify(customerService).findEntity(customerId);
        verify(productService, never()).findEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found during order creation")
    void shouldThrowNotFoundExceptionWhenProductNotFoundDuringOrderCreation() {
        when(customerService.findEntity(customerId)).thenReturn(customer);
        when(productService.findEntity(productId)).thenThrow(new NotFoundException("Product not found"));

        assertThatThrownBy(() -> orderService.create(createRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Product not found");

        verify(customerService).findEntity(customerId);
        verify(productService).findEntity(productId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create order with multiple items")
    void shouldCreateOrderWithMultipleItems() {
        Product product2 = new Product();
        product2.setId(UUID.randomUUID());
        product2.setName("Another Product");
        product2.setPrice(new BigDecimal("19.99"));

        OrderItemRequest itemRequest2 = new OrderItemRequest();
        itemRequest2.productId = product2.getId();
        itemRequest2.quantity = 1;
        
        createRequest.items = new ArrayList<>(createRequest.items);
        createRequest.items.add(itemRequest2);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(order);
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(1);
        orderItem2.setUnitPrice(new BigDecimal("19.99"));
        order.getItems().add(orderItem2);

        when(customerService.findEntity(customerId)).thenReturn(customer);
        when(productService.findEntity(productId)).thenReturn(product);
        when(productService.findEntity(product2.getId())).thenReturn(product2);
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(order);
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        OrderResponse result = orderService.create(createRequest);

        assertThat(result.items).hasSize(2);
        assertThat(result.total).isEqualTo(new BigDecimal("79.97"));
    }

    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatusSuccessfully() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        PurchaseOrder updatedOrder = new PurchaseOrder();
        updatedOrder.setId(orderId);
        updatedOrder.setCustomer(customer);
        updatedOrder.setStatus(OrderStatus.PROCESSING);
        updatedOrder.setCreatedAt(order.getCreatedAt());
        updatedOrder.setItems(order.getItems());
        
        when(orderRepository.save(any(PurchaseOrder.class))).thenReturn(updatedOrder);
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        OrderResponse result = orderService.updateStatus(orderId, updateStatusRequest);

        assertThat(result.status).isEqualTo(OrderStatus.PROCESSING);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent order")
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistentOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(orderId, updateStatusRequest))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should list all orders when status is null")
    void shouldListAllOrdersWhenStatusIsNull() {
        PurchaseOrder order2 = new PurchaseOrder();
        order2.setId(UUID.randomUUID());
        order2.setCustomer(customer);
        order2.setStatus(OrderStatus.PROCESSING);
        order2.setCreatedAt(OffsetDateTime.now());

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order, order2));
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        List<OrderResponse> result = orderService.list(null);

        assertThat(result).hasSize(2);
        verify(orderRepository).findAll();
        verify(orderRepository, never()).findByStatus(any());
    }

    @Test
    @DisplayName("Should list orders by status")
    void shouldListOrdersByStatus() {
        when(orderRepository.findByStatus(OrderStatus.CREATED)).thenReturn(Arrays.asList(order));
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        List<OrderResponse> result = orderService.list(OrderStatus.CREATED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status).isEqualTo(OrderStatus.CREATED);
        verify(orderRepository).findByStatus(OrderStatus.CREATED);
        verify(orderRepository, never()).findAll();
    }

    @Test
    @DisplayName("Should get order by id")
    void shouldGetOrderById() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        OrderResponse result = orderService.get(orderId);

        assertThat(result).isNotNull();
        assertThat(result.id).isEqualTo(orderId);
        assertThat(result.status).isEqualTo(OrderStatus.CREATED);
        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when getting non-existent order")
    void shouldThrowNotFoundExceptionWhenGettingNonExistentOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.get(orderId))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Order not found");

        verify(orderRepository).findById(orderId);
    }

    @Test
    @DisplayName("Should calculate total correctly")
    void shouldCalculateTotalCorrectly() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        CustomerDto customerDto = new CustomerDto();
        customerDto.id = customerId;
        when(customerService.get(customerId)).thenReturn(customerDto);

        OrderResponse result = orderService.get(orderId);

        assertThat(result.total).isEqualTo(new BigDecimal("59.98"));
        assertThat(result.items.get(0).total).isEqualTo(new BigDecimal("59.98"));
    }

    @Test
    @DisplayName("Should return empty list when no orders exist")
    void shouldReturnEmptyListWhenNoOrdersExist() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        List<OrderResponse> result = orderService.list(null);

        assertThat(result).isEmpty();
        verify(orderRepository).findAll();
    }
}

