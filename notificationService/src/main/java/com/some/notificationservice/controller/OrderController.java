package com.some.notificationservice.controller;

import com.some.notificationservice.model.entity.OrderEntity;
import com.some.notificationservice.model.entity.OrderItemEntity;
import com.some.notificationservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public Page<OrderEntity> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @GetMapping("/products/{id}")
    public Page<OrderItemEntity> getProductsByOrdertId(@PathVariable UUID id, Pageable pageable) {
        return orderService.getAllOrderItemsByOrderId(pageable, id);
    }

    @GetMapping("/users/{id}")
    public Page<OrderEntity> getOrdersByUserId(@PathVariable UUID userId, Pageable pageable) {
        return orderService.getAllOrdersByUserId(pageable, userId);
    }
}
