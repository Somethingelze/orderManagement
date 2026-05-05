package com.some.authservice.controllers;

import com.some.authservice.model.dto.Request.LoginRequestDto;
import com.some.authservice.model.dto.Request.RegistrationRequestDto;
import com.some.authservice.model.dto.Response.AuthenticationResponseDto;
import com.some.authservice.services.impl.AuthServiceImpl;
import com.some.authservice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto registrationDto) {
        if (userService.existsByUsername(registrationDto.username())) {
            log.warn("Попытка регистрации существующего пользователя: {}", registrationDto.username());
            return ResponseEntity.badRequest().body("Имя пользователя " + registrationDto.username() + " уже занято");
        }

        AuthenticationResponseDto response = authService.register(registrationDto);
        log.debug("Успешно зарегистрирован пользователь: {}", registrationDto.username());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginRequestDto request) {
        log.debug("Запрос на вход пользователя: {}", request.username());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }
}