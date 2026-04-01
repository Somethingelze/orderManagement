package com.some.orderservice.model.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItem (
        UUID id,
        String name,
        BigDecimal price,
        BigDecimal sale,
        BigDecimal totalPrice
){
}
