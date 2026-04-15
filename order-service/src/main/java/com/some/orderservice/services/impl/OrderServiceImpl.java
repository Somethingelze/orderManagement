package com.some.orderservice.services.impl;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.UserPrincipal;
import com.some.commonlib.model.event.OrderEvent;
import com.some.commonlib.util.JwtUtils;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import com.some.orderservice.repositories.OrderItemRepository;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Loggable
public class OrderServiceImpl implements OrderService {

    private final InventoryGrpcClient inventoryClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final JwtUtils jwtUtils;


    @Override
    public OrderResponseDto processOrder(OrderRequestDto orderRequestDto) {
        ProductRequestDto productRequestDto = orderMapper.toProductRequestDto(orderRequestDto);

        OrderEntity orderEntity = checkAvailability(productRequestDto);
        sendOrderEvent(orderEntity);
        return orderMapper.toOrderResponseDto(orderEntity);
    }

    @Override
    public OrderEntity checkAvailability(ProductRequestDto productRequestDto) {

        List<OrderItemEntity> orderItems = inventoryClient.checkAvailability(productRequestDto)
                .getItemsList()
                .stream()
                .map(orderMapper::toOrderItemEntity)
                .toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .userId(user.id())
                .userEmail(user.email())
                .orderItems(orderItems)
                .totalPrice(totalPrice)
                .build();

        orderItems.forEach(orderItemEntity -> {orderItemEntity.setOrder(order);});

        return orderRepository.save(order);
    }

    @Override
    public void sendOrderEvent(OrderEntity orderEntity) {

        orderEntity.getOrderItems().forEach(orderMapper::toOrderItem);

        OrderEvent orderEvent = orderMapper.toOrderEvent(orderEntity);

        log.info("Sending event: {}", orderEvent);
        kafkaTemplate.send("order-event", orderEvent);
    }

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
