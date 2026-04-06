package com.some.orderservice.model.entities;

import com.some.orderservice.model.enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record Order(
        UUID id,
        UUID orderId,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice,
        Status status
){
}
