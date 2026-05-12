package com.some.authservice.model.dto.Request;

public record LoginRequestDto(
      String username,
      String password
) {
}
