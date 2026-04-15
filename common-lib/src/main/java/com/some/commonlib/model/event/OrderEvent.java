package com.some.commonlib.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.some.commonlib.model.entity.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderEvent(
        @JsonProperty("id") UUID id,
        @JsonProperty("userId") UUID userId,
        @JsonProperty("userEmail") String userEmail,
        @JsonProperty("orderItems") List<OrderItem> orderItems,
        @JsonProperty("totalPrice") BigDecimal totalPrice
) {}
