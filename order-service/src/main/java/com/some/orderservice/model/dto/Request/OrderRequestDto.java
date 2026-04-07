package com.some.orderservice.model.dto.Request;

import lombok.Data;

import java.util.Map;

@Data
public class OrderRequestDto  {
        String orderId;
        Map<String, Long> orderItems;

}
