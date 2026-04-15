package com.some.commonlib.model.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
){
}
