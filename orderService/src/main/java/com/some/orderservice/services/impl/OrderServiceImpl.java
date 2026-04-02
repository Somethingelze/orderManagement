package com.some.orderservice.services.impl;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.entities.OrderItem;
import com.some.orderservice.model.event.OrderEvent;
import com.some.orderservice.model.entities.Order;
import com.some.orderservice.services.OrderService;
import com.some.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final InventoryGrpcClient inventoryClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final UserService userService;
    private final OrderMapper orderMapper;


    @Override
    @Transactional
    public void processOrder(ProductRequestDto productRequestDto) {
        Order order = checkAvailability(productRequestDto);
        sendOrderEvent(order);
    }

    @Override
    public Order checkAvailability(ProductRequestDto productRequestDto) {

        List<OrderItem> orderItems = inventoryClient.checkAvailability(productRequestDto)
                .getItemsList()
                .stream()
                .map(orderMapper::toOrderItem)
                .toList();
        
        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Order.builder()
                .id(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .userId(userService.getCurrentUserId())
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void sendOrderEvent(Order order) {
        OrderEvent orderEvent = orderMapper.toOrderEvent(order);
        kafkaTemplate.send("order-event", orderEvent);
    }
}
