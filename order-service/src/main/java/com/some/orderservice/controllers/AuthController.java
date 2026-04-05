package com.some.orderservice.controllers;

import com.some.orderservice.model.dto.Request.LoginRequestDto;
import com.some.orderservice.model.dto.Request.RegistrationRequestDto;
import com.some.orderservice.model.dto.Responce.AuthenticationResponseDto;
import com.some.orderservice.services.AuthentificationService;
import com.some.orderservice.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthentificationService authenticationService;
    private final UserService userService;

    @PostMapping("/reg")
    public ResponseEntity<String> register(
            @RequestBody RegistrationRequestDto registrationDto) {

        if (userService.existsByUsername(registrationDto.getUsername())) {
            log.info("Попытка регистрации существующего пользователя с именем: "  + registrationDto.getUsername());
            return ResponseEntity.badRequest().body("Имя пользователя " + registrationDto.getUsername() + " уже занято");
        }

        authenticationService.register(registrationDto);

        log.info("Успешно зарегистрирован пользователь " + registrationDto.getUsername());
        return ResponseEntity.ok("Регистрация пользователя " + registrationDto.getUsername() + " прошла успешно");
    }

    @PostMapping("/login")
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
