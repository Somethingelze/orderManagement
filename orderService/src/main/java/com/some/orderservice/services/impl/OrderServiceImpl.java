package com.some.orderservice.services.impl;

import com.some.grpc.inventory.ProductResponseDto;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.model.event.OrderEvent;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final InventoryGrpcClient inventoryClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;


    @Override
    public void processOrder(String productId, Long quantity) {
        OrderEntity orderEntity = checkAvailability(productId, quantity);
        saveOrder(orderEntity);
        sendOrderEvent(orderEntity);
    }

    @Transactional
    @Override
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        return orderRepository.save(orderEntity);
    }

    @Override
    public OrderEntity checkAvailability(String productId, Long quantity) {
        ProductResponseDto response = inventoryClient.checkAvailability(productId, quantity);

        BigDecimal price = BigDecimal.valueOf(response.getPricePennies(), 2);
        BigDecimal sale = BigDecimal.valueOf(response.getSalePennies(), 2);

        BigDecimal totalPrice = price.subtract(sale)
                .multiply(BigDecimal.valueOf(quantity));

        return OrderEntity.builder()
                .id(UUID.fromString(response.getId()))
                .quantity(quantity)
                .price(price)
                .sale(sale)
                .totalPrice(totalPrice)
                .build();
    }

    @Override
    public void sendOrderEvent(OrderEntity orderEntity) {
        OrderEvent orderEvent = OrderEvent.builder()
                .orderId(orderEntity.getId())
                .quantity(orderEntity.getQuantity())
                .price(orderEntity.getPrice())
                .sale(orderEntity.getSale())
                .totalPrice(orderEntity.getTotalPrice())
                .build();

        kafkaTemplate.send("order-event", orderEvent);
    }
}
