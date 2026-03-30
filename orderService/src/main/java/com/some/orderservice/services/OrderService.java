package com.some.orderservice.services;

import com.some.orderservice.model.entities.OrderEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OrderService {

    void processOrder(String productId, Long quantity);

    @Transactional
    OrderEntity saveOrder(OrderEntity orderEntity);

    OrderEntity checkAvailability(String productId, Long quantity);

    void sendOrderEvent(OrderEntity orderEntity);
}
