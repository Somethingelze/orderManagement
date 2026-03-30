package com.some.orderservice.controllers;

import com.some.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public void createOrder (@RequestParam String productId,
                             @RequestParam Long quantity)   {
        orderService.processOrder(productId, quantity);
    }

}
