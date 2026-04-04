package com.some.orderservice.services.impl;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.entities.Order;
import com.some.orderservice.model.entities.OrderItem;
import com.some.orderservice.model.event.OrderEvent;
import com.some.orderservice.services.OrderService;
import com.some.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final InventoryGrpcClient inventoryClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final UserService userService;
    private final OrderMapper orderMapper;


    @Transactional
    @Override
    public void processOrder(OrderRequestDto orderRequestDto) {

        log.info("Start processing Order Request " + orderRequestDto.getOrderId());

        ProductRequestDto productRequestDto = orderMapper.toProductRequestDto(orderRequestDto);
        Order order = checkAvailability(productRequestDto);
        sendOrderEvent(order);
    }

    @Override
    public Order checkAvailability(ProductRequestDto productRequestDto) {
        log.info("Received request to check availability " + productRequestDto.getOrderId());

        List<OrderItem> orderItems = inventoryClient.checkAvailability(productRequestDto)
                .getItemsList()
                .stream()
                .map(orderMapper::toOrderItem)
                .toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItem::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Order.builder()
                .id(UUID.fromString(productRequestDto.getOrderId()))
                .orderId(UUID.fromString(productRequestDto.getOrderId()))
                .userId(userService.getCurrentUserId())
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void sendOrderEvent(Order order) {
        log.info("Order event {} send to notificationService ", order.orderId());
        OrderEvent orderEvent = orderMapper.toOrderEvent(order);
        kafkaTemplate.send("order-event", orderEvent);
    }
}
