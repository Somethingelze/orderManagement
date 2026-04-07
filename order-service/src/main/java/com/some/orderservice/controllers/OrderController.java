package com.some.orderservice.controllers;

import com.some.orderservice.annotations.Loggable;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/orders/create")
@RequiredArgsConstructor
@Loggable
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder (@Valid @RequestBody OrderRequestDto orderRequestDto)   {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.processOrder(orderRequestDto));
    }
}
