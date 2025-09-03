package com.example.delivery.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderStatus Enum Tests")
class OrderStatusTest {

    @Test
    @DisplayName("Should have all expected status values")
    void shouldHaveAllExpectedStatusValues() {
        OrderStatus[] expectedStatuses = {
            OrderStatus.CREATED,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPED,
            OrderStatus.DELIVERED,
            OrderStatus.CANCELED
        };

        OrderStatus[] actualStatuses = OrderStatus.values();

        assertThat(actualStatuses).containsExactly(expectedStatuses);
        assertThat(actualStatuses).hasSize(5);
    }

    @Test
    @DisplayName("Should have correct string representation")
    void shouldHaveCorrectStringRepresentation() {
        assertThat(OrderStatus.CREATED.toString()).isEqualTo("CREATED");
        assertThat(OrderStatus.PROCESSING.toString()).isEqualTo("PROCESSING");
        assertThat(OrderStatus.SHIPPED.toString()).isEqualTo("SHIPPED");
        assertThat(OrderStatus.DELIVERED.toString()).isEqualTo("DELIVERED");
        assertThat(OrderStatus.CANCELED.toString()).isEqualTo("CANCELED");
    }

    @Test
    @DisplayName("Should support valueOf conversion")
    void shouldSupportValueOfConversion() {
        assertThat(OrderStatus.valueOf("CREATED")).isEqualTo(OrderStatus.CREATED);
        assertThat(OrderStatus.valueOf("PROCESSING")).isEqualTo(OrderStatus.PROCESSING);
        assertThat(OrderStatus.valueOf("SHIPPED")).isEqualTo(OrderStatus.SHIPPED);
        assertThat(OrderStatus.valueOf("DELIVERED")).isEqualTo(OrderStatus.DELIVERED);
        assertThat(OrderStatus.valueOf("CANCELED")).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("Should have correct ordinal values")
    void shouldHaveCorrectOrdinalValues() {
        assertThat(OrderStatus.CREATED.ordinal()).isEqualTo(0);
        assertThat(OrderStatus.PROCESSING.ordinal()).isEqualTo(1);
        assertThat(OrderStatus.SHIPPED.ordinal()).isEqualTo(2);
        assertThat(OrderStatus.DELIVERED.ordinal()).isEqualTo(3);
        assertThat(OrderStatus.CANCELED.ordinal()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should support equality comparison")
    void shouldSupportEqualityComparison() {
        OrderStatus status1 = OrderStatus.CREATED;
        OrderStatus status2 = OrderStatus.CREATED;
        OrderStatus status3 = OrderStatus.PROCESSING;

        assertThat(status1).isEqualTo(status2);
        assertThat(status1).isNotEqualTo(status3);
        assertThat(status1 == status2).isTrue();
        assertThat(status1 == status3).isFalse();
    }
}

