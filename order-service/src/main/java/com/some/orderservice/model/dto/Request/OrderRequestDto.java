package com.some.orderservice.model.dto.Request;

import lombok.Builder;

import java.util.Map;

@Builder
public record OrderRequestDto  (
        Map<String, Long> orderItems
){}
