package com.some.orderservice.model.dto.Request;

import lombok.Data;

import java.util.Map;

@Data
public class OrderRequestDto  {
        Map<String, Long> orderItems;
}
