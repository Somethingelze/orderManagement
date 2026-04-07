package com.some.orderservice.services;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface OrderService {


    OrderResponseDto processOrder(OrderRequestDto orderRequestDto);

    Order checkAvailability(ProductRequestDto productRequestDto);

    void sendOrderEvent(Order order);
}
