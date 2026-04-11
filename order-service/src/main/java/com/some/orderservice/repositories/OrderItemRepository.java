package com.some.orderservice.repositories;

import com.some.orderservice.model.entities.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
    Page<OrderItemEntity> findAllByOrderId(UUID orderId, Pageable pageable);
}
