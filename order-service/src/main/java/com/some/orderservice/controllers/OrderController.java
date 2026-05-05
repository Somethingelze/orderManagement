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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Loggable
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder (@Valid @RequestBody OrderRequestDto orderRequestDto)   {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.processOrder(orderRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<OrderEntity>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok().body(orderService.getAllOrders(pageable));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<OrderItemEntity>> getProductsByOrderId(@RequestParam UUID orderId, Pageable pageable) {
        return ResponseEntity.ok().body(orderService.getAllOrderItemsByOrderId(pageable, orderId));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<OrderEntity>> getOrdersByUserId(@RequestParam UUID userId, Pageable pageable) {
        return ResponseEntity.ok().body(orderService.getAllOrdersByUserId(pageable, userId));
    }
}
