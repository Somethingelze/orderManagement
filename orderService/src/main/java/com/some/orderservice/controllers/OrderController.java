package com.some.orderservice.controllers;

import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders/create")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public void createOrder (@RequestBody OrderRequestDto orderRequestDto)   {
        orderRequestDto.setOrderId(UUID.randomUUID().toString());
        log.info("Received request to create order " + orderRequestDto.getOrderId());

        orderService.processOrder(orderRequestDto);
    }
}
