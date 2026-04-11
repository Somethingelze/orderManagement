package com.some.notificationservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record Order(
        UUID id,
        UUID orderId,
        UUID userId,
        String userEmail,
        List<OrderItem> orderItems,
        BigDecimal totalPrice
){
}
