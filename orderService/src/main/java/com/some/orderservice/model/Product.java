package com.some.orderservice.model;

public record Product (
        Long id,
        String name,
        Long quantity
) {}
