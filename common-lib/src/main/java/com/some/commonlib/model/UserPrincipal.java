package com.some.commonlib.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserPrincipal(
    UUID id,
    String username,
    String email
) {}