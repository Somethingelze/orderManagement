package com.some.orderservice.services;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.model.entities.Order;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {


    void processOrder(ProductRequestDto productRequestDto);

    Order checkAvailability(ProductRequestDto productRequestDto);

    void sendOrderEvent(Order order);
}
