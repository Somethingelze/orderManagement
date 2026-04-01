package com.some.orderservice.controllers;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public void createOrder (@RequestBody ProductRequestDto productRequestDto)   {
        orderService.processOrder(productRequestDto);
    }

}
