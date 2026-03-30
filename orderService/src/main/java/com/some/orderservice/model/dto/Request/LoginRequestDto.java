package com.some.orderservice.model.dto.Request;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String username;
    private String password;
}
