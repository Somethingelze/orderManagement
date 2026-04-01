package com.some.orderservice.model.entities;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record Order(
        UUID id,
        UUID orderId,
        UUID userId,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
){
}
