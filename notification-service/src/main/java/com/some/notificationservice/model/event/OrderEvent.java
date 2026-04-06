package com.some.notificationservice.model.event;

import com.some.notificationservice.model.entity.OrderItemEntity;
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
        List<OrderItemEntity> orderItems,
        BigDecimal totalPrice
) {}
