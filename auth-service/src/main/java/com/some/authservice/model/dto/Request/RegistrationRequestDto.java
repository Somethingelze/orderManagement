package com.some.authservice.model.dto.Request;


public record RegistrationRequestDto (
        String username,
        String password,
        String email
) {
}