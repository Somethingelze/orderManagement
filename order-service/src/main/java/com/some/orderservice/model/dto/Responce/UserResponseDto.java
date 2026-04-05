package com.some.orderservice.model.dto.Responce;

import com.some.orderservice.model.enums.Role;

public record UserResponseDto (
    String username,
    String email,
    Role role
) {}
