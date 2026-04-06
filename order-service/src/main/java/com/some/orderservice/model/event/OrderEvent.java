package com.some.orderservice.model.event;

import com.some.orderservice.model.entities.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderEvent(
        UUID id,
        UUID orderId,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
) {}
