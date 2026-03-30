package com.some.orderservice.model.dto.Request;

import com.some.orderservice.model.enums.Role;

public record UserRequestDto(
        String username,
        String email,
        Role role
) {
}
