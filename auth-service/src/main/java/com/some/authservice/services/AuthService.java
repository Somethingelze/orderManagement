package com.some.authservice.services;

import com.some.authservice.model.dto.Request.LoginRequestDto;
import com.some.authservice.model.dto.Request.RegistrationRequestDto;
import com.some.authservice.model.dto.Response.AuthenticationResponseDto;
import com.some.authservice.model.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public abstract class AuthService {
    public abstract AuthenticationResponseDto register(RegistrationRequestDto dto);

    public abstract AuthenticationResponseDto authenticate(LoginRequestDto request);

    public abstract ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request);

    protected abstract AuthenticationResponseDto generateAuthResponse(UserEntity user);
}
