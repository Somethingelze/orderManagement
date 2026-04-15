package com.some.commonlib.model.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItem (
        UUID id,
        UUID productId,
        UUID orderId,
        String productName,
        BigDecimal price,
        BigDecimal sale,
        BigDecimal totalPrice,
        boolean available
){
}
