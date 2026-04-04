package com.some.orderservice.model.dto.Request;

import lombok.Data;

@Data
public class RegistrationRequestDto {

    private String username;
    private String password;
    private String email;
}