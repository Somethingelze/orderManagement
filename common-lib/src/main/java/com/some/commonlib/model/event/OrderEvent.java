package com.some.commonlib.model.event;

import com.some.commonlib.model.OrderItem;
import com.some.commonlib.model.enums.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderEvent(
        UUID id,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice,
        Status status,
        List<String> unavailableProducts
) {}
