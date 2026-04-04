package com.some.orderservice.model.dto.Request;

public record UserRequestDto(
        String username,
        String password,
        String email) {
}
