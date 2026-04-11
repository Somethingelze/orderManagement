package com.some.authservice.model.dto.Response;

public record AuthenticationResponseDto(
        String accessToken,
        String refreshToken
) {
}