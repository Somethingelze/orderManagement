package com.some.orderservice.services.impl;

import com.some.commonlib.annotations.Loggable;
import com.some.commonlib.model.enums.Status;
import com.some.grpc.inventory.*;
import com.some.orderservice.grpc.InventoryGrpcClient;
import com.some.orderservice.mappers.OrderMapper;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import com.some.orderservice.repositories.OrderItemRepository;
import com.some.orderservice.repositories.OrderRepository;
import com.some.orderservice.services.OrderService;
import com.some.orderservice.services.OutboxService;
import com.some.orderservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Loggable
public class OrderServiceImpl implements OrderService {

    private final InventoryGrpcClient inventoryClient;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final OutboxService outboxService;

    @Override
    @Transactional
    public OrderResponseDto processOrder(OrderRequestDto orderRequestDto) {
        log.info("Received Order Request {}",  orderRequestDto);

        OrderEntity orderEntity = OrderEntity.builder()
                .id(UUID.randomUUID())
                .userId(userService.getUserId())
                .userEmail(userService.getUSerEmail())
                .status(Status.CREATED)
                .build();
        outboxService.saveAndOutbox(orderEntity);
        log.info("Order Created for {}", orderEntity.toString());

        ConfirmedOrderId confirmedOrderId = ConfirmedOrderId.newBuilder()
                .setId(orderEntity.getId().toString())
                .build();
        try {
        ProductRequestDto productRequestDto = ProductRequestDto.newBuilder()
                .setOrderId(String.valueOf(orderEntity.getId()))
                .putAllOrderItems(orderRequestDto.orderItems())
                .build();

        AvailabilityProductsDto  availabilityProductsDto = checkAvailability(productRequestDto, orderEntity);

         if (availabilityProductsDto.getAvailableProductsMap().isEmpty()) {
             return OrderResponseDto.builder()
                     .orderId(orderEntity.getId())
                     .build();
         }

        collectOrder(availabilityProductsDto, orderEntity);

        confirmOrder(confirmedOrderId, orderEntity);

        } catch (Exception e) {
            cancelConfirmation(confirmedOrderId, orderEntity);
        }

        log.info("Order confirm products for {}", orderEntity);
        return orderMapper.toOrderResponseDto(orderEntity);
    }

    public AvailabilityProductsDto checkAvailability(ProductRequestDto productRequestDto, OrderEntity orderEntity)  {
        AvailabilityProductsDto availabilityProductsDto = inventoryClient.checkAvailability(productRequestDto);

        if (availabilityProductsDto.getAvailableProductsMap().isEmpty()) {
            orderEntity.setStatus(Status.REJECTED);
            orderEntity.setUnavailableProductsIds(availabilityProductsDto.getUnavailableProductsList());
            outboxService.saveAndOutbox(orderEntity);
            log.info("Order Rejected for {}. Unavailable products: {}", orderEntity.getId(), orderEntity.getUnavailableProductsIds());
            return AvailabilityProductsDto.newBuilder()
                    .setIsAvailable(false)
                    .addAllUnavailableProducts(availabilityProductsDto.getUnavailableProductsList())
                    .build();
        }

        if (!availabilityProductsDto.getIsAvailable()) {
            orderEntity.setStatus(Status.PARTIAL_RESERVED);
            orderEntity.setUnavailableProductsIds(availabilityProductsDto.getUnavailableProductsList());
            log.info("Order Partial reserved for {}. Unavailable products: {}. Available products: {}",
                    orderEntity.getId(), availabilityProductsDto.getUnavailableProductsList(), availabilityProductsDto.getAvailableProductsMap());
            outboxService.saveAndOutbox(orderEntity);
        } else {
            orderEntity.setStatus(Status.RESERVED);
            log.info("Order {} successfully reserved for products {}", orderEntity.getId(), availabilityProductsDto.getAvailableProductsMap());
            outboxService.saveAndOutbox(orderEntity);
        }
        log.info("Order check availability for {}", orderEntity);
        return availabilityProductsDto;
    }

    public void collectOrder(AvailabilityProductsDto availabilityProductsDto, OrderEntity orderEntity)    {
        List<OrderItemEntity> orderItems = inventoryClient.collectOrder(availabilityProductsDto).getItemsList()
                .stream()
                .map(orderMapper::toOrderItemEntity)
                .toList();

        BigDecimal totalPrice = orderItems.stream()
                .map(OrderItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderEntity.setOrderItems(orderItems);
        orderEntity.setTotalPrice(totalPrice);
        orderEntity.setStatus(Status.COLLECTED);

        orderItems.forEach(orderItemEntity -> {
            orderItemEntity.setOrder(orderEntity);
        });

        outboxService.saveAndOutbox(orderEntity);
        log.info("Order collect products for {}", orderEntity);
    }

    public void confirmOrder(ConfirmedOrderId confirmedOrderId, OrderEntity orderEntity) {
        inventoryClient.confirmOrder(confirmedOrderId);
        orderEntity.setStatus(Status.SUCCESS);
        outboxService.saveAndOutbox(orderEntity);
    }

    public void cancelConfirmation(ConfirmedOrderId confirmedOrderId, OrderEntity orderEntity) {
        inventoryClient.cancelConfirmation(confirmedOrderId);
        orderEntity.setStatus(Status.ERROR);
        outboxService.saveAndOutbox(orderEntity);
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
    public Page<OrderEntity> getAllOrdersByUserId(Pageable pageable, UUID userId) {
        return orderRepository.findAllOrdersByUserId(userId, pageable);
    }
}
