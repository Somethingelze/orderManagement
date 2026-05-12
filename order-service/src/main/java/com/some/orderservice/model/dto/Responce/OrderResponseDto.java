package com.some.orderservice.model.dto.Responce;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderResponseDto(
        @NotNull
        UUID orderId

) {
}
