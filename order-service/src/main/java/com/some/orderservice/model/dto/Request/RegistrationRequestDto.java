package com.some.orderservice.model.dto.Request;


public record RegistrationRequestDto (
        String username,
        String password,
        String email
) {
}