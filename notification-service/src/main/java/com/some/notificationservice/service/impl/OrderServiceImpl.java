package com.some.notificationservice.service.impl;

import com.some.notificationservice.annotations.Loggable;
import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
import com.some.notificationservice.repository.OrderItemRepository;
import com.some.notificationservice.repository.OrderRepository;
import com.some.notificationservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Loggable
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Page<OrderEntity> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<OrderItemEntity> getAllOrderItemsByOrderId(Pageable pageable, UUID orderId) {
        return orderItemRepository.findAllByOrderId(orderId, pageable);
    }

    @Override
    public Page<OrderEntity> getAllOrdersByUserId(Pageable pageable, UUID userId)  {
        return orderRepository.findAllOrdersByUserId(userId, pageable);
    }
}
