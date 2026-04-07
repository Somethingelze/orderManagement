package com.some.orderservice.services;

import com.some.orderservice.model.dto.Request.LoginRequestDto;
import com.some.orderservice.model.dto.Request.RegistrationRequestDto;
import com.some.orderservice.model.dto.Responce.AuthenticationResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface AuthentificationService {
    void register(RegistrationRequestDto request);
    AuthenticationResponseDto authenticate(LoginRequestDto request);
    ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response);
}
