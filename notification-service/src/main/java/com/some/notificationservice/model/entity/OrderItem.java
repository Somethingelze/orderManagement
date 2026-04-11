package com.some.notificationservice.model.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItem (
        UUID id,
        UUID productId,
        UUID orderId,
        String name,
        BigDecimal price,
        BigDecimal sale,
        BigDecimal totalPrice,
        boolean available
){
}
