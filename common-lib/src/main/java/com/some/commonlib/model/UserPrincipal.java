package com.some.commonlib.model;

import java.util.UUID;

public record UserPrincipal(
    UUID id,
    String username,
    String email
) {}