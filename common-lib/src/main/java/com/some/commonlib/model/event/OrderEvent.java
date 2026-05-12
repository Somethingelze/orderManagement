package com.some.commonlib.model.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.some.commonlib.model.OrderItem;
import com.some.commonlib.model.enums.Status;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Jacksonized
public record OrderEvent(
        @JsonProperty("eventId")UUID eventId,
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("status") Status status,
        @JsonProperty("userId") UUID userId,
        @JsonProperty("userEmail") String userEmail,
        @JsonProperty("totalPrice") BigDecimal totalPrice,
        @JsonProperty("orderItems") List<OrderItem> orderItems,
        @JsonProperty("unavailableProducts") List<String> unavailableProducts
) {}
