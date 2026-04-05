package com.some.notificationservice.service;

import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface OrderService {
    Page<OrderEntity> getAllOrders (Pageable pageable);

    Page<OrderItemEntity> getAllOrderItemsByOrderId(Pageable pageable, UUID orderId);

    Page<OrderEntity> getAllOrdersByUserId(Pageable pageable, UUID userId);
}
