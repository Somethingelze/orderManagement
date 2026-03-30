package com.some.orderservice.controllers;

import com.some.orderservice.model.dto.Request.LoginRequestDto;
import com.some.orderservice.model.dto.Request.RegistrationRequestDto;
import com.some.orderservice.model.dto.Responce.AuthenticationResponseDto;
import com.some.orderservice.services.AuthentificationService;
import com.some.orderservice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthentificationService authenticationService;
    private UserService userService;

    @GetMapping("/reg")
    public ResponseEntity<String> register(
            @RequestBody RegistrationRequestDto registrationDto) {

        if (userService.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.badRequest().body("Имя пользователя уже занято");
        }

        authenticationService.register(registrationDto);

        return ResponseEntity.ok("Регистрация прошла успешно");
    }

    @GetMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        return authenticationService.refreshToken(request, response);
    }
}
