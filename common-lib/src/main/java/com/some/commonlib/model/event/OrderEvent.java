package com.some.commonlib.model.event;

import com.some.commonlib.model.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderEvent(
        UUID id,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
) {}
