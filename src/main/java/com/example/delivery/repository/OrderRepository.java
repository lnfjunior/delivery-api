package com.example.delivery.repository;

import com.example.delivery.domain.OrderStatus;
import com.example.delivery.domain.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findByStatus(OrderStatus status);
}
