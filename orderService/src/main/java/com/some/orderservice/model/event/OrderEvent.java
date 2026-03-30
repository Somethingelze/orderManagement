package com.some.orderservice.model.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderEvent(
        UUID orderId,
//        UUID productId,
        Long quantity,
        BigDecimal price,
        BigDecimal sale,
        BigDecimal totalPrice
//        UUID userId
) {}
