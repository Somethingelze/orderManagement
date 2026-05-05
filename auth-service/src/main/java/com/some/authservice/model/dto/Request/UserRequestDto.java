package com.some.authservice.model.dto.Request;

public record UserRequestDto(
        String username,
        String password,
        String email) {
}
