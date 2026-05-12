package com.some.commonlib.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Jacksonized
public record OrderItem (
        @JsonProperty("id") UUID id,
        @JsonProperty("productId") UUID productId,
        @JsonProperty("orderId") UUID orderId,
        @JsonProperty("productName") String productName,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("sale") BigDecimal sale,
        @JsonProperty("totalPrice") BigDecimal totalPrice,
        @JsonProperty("available") boolean available
){
}
