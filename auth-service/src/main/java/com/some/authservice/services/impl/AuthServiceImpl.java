package com.some.authservice.services.impl;

import com.some.authservice.model.dto.Request.LoginRequestDto;
import com.some.authservice.model.dto.Request.RegistrationRequestDto;
import com.some.authservice.model.dto.Response.AuthenticationResponseDto;
import com.some.authservice.model.entities.UserEntity;
import com.some.authservice.model.enums.Role;
import com.some.authservice.repositories.UserRepository;
import com.some.authservice.services.AuthService;
import com.some.commonlib.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthenticationResponseDto register(RegistrationRequestDto dto) {
        UserEntity user = UserEntity.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        return generateAuthResponse(user);
    }

    @Override
    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return generateAuthResponse(user);
    }

    @Override
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String oldToken = authHeader.substring(7);
        String username = jwtUtils.extractUsername(oldToken);

        if (username != null && jwtUtils.isTokenValid(oldToken)) {
            UserEntity user = userRepository.findByUsername(username).orElseThrow();
            return ResponseEntity.ok(generateAuthResponse(user));
        }

        return ResponseEntity.status(401).build();
    }

    @Override
    protected AuthenticationResponseDto generateAuthResponse(UserEntity user) {
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        extraClaims.put("roles", roles);
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("email", user.getEmail());

        String accessToken = jwtUtils.generateToken(user.getUsername(), extraClaims);
        String refreshToken = jwtUtils.generateToken(user.getUsername(), new HashMap<>());

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }
}