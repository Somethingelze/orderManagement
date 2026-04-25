package com.some.orderservice.services;

import com.some.commonlib.model.enums.Status;
import com.some.commonlib.model.event.OrderEvent;
import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {


    OrderResponseDto processOrder(OrderRequestDto orderRequestDto);

    OrderEntity checkAvailability(ProductRequestDto productRequestDto);

    Status setOrderStatus(List<OrderItemEntity> orderItems);

    List<String> getUnavailableProductsName(OrderEntity orderEntity);

    OrderEvent sendOrderEvent(OrderEntity orderEntity);

    Page<OrderEntity> getAllOrders (Pageable pageable);

    Page<OrderItemEntity> getAllOrderItemsByOrderId(Pageable pageable, UUID orderId);

    Page<OrderEntity> getAllOrdersByUserId(Pageable pageable, UUID userId);
}
