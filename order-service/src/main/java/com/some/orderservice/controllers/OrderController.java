package com.some.orderservice.controllers;

import com.some.commonlib.annotations.Loggable;
import com.some.orderservice.model.dto.Request.OrderRequestDto;
import com.some.orderservice.model.dto.Responce.OrderResponseDto;
import com.some.orderservice.model.entities.OrderEntity;
import com.some.orderservice.model.entities.OrderItemEntity;
import com.some.orderservice.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/all")
    public Page<OrderEntity> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/products/{id}")
    public Page<OrderItemEntity> getProductsByOrdertId(@PathVariable UUID id, Pageable pageable) {
        return orderService.getAllOrderItemsByOrderId(pageable, id);
    }

    @GetMapping("/users/{id}")
    public Page<OrderEntity> getOrdersByUserId(@PathVariable UUID id, Pageable pageable) {
        return orderService.getAllOrdersByUserId(pageable, id);
    }
}
