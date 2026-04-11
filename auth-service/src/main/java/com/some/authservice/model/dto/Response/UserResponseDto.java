package com.some.authservice.model.dto.Response;


import com.some.authservice.model.enums.Role;

public record UserResponseDto (
    String username,
    String email,
    Role role
) {}
