package com.some.commonlib.dto;

import java.util.UUID;

public record UserPrincipal(
    UUID id,
    String username,
    String email
) {}