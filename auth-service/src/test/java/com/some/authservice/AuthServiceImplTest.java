package com.some.authservice;

import com.some.authservice.model.dto.Request.LoginRequestDto;
import com.some.authservice.model.dto.Request.RegistrationRequestDto;
import com.some.authservice.model.dto.Response.AuthenticationResponseDto;
import com.some.authservice.model.entities.UserEntity;
import com.some.authservice.model.enums.Role;
import com.some.authservice.repositories.UserRepository;
import com.some.authservice.services.impl.AuthServiceImpl;
import com.some.commonlib.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String USERNAME = "testUser";
    private final String PASSWORD = "password123";
    private final String EMAIL = "test@example.com";

    @Nested
    @DisplayName("User Registration")
    class RegisterTests {
        @Test
        void register_shouldSaveUserAndReturnTokens() {
            RegistrationRequestDto dto = new RegistrationRequestDto(USERNAME, PASSWORD, EMAIL);

            given(passwordEncoder.encode(PASSWORD)).willReturn("encoded");

            // Используем matchers для разделения Access и Refresh
            // Access token: мапа с ролями и userId (не пустая)
            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && !m.isEmpty())))
                    .willReturn("access-token");
            // Refresh token: пустая мапа
            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && m.isEmpty())))
                    .willReturn("refresh-token");

            given(userRepository.save(any(UserEntity.class))).willAnswer(inv -> {
                UserEntity user = inv.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });

            AuthenticationResponseDto response = authService.register(dto);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
        }
    }

    @Nested
    @DisplayName("User Authentication")
    class AuthenticateTests {
        @Test
        void authenticate_validCredentials_returnsTokens() {
            LoginRequestDto request = new LoginRequestDto(USERNAME, PASSWORD);
            UserEntity user = UserEntity.builder()
                    .id(UUID.randomUUID())
                    .username(USERNAME)
                    .email(EMAIL)
                    .role(Role.ROLE_USER)
                    .build();

            given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && !m.isEmpty())))
                    .willReturn("access-token");
            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && m.isEmpty())))
                    .willReturn("refresh-token");

            AuthenticationResponseDto response = authService.authenticate(request);

            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }
    }

    @Nested
    @DisplayName("Token Refresh Operations")
    class RefreshTokenTests {
        @Test
        void refreshToken_validToken_returnsNewTokens() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            UserEntity user = UserEntity.builder()
                    .id(UUID.randomUUID())
                    .username(USERNAME)
                    .role(Role.ROLE_USER)
                    .build();

            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer old");
            given(jwtUtils.extractUsername("old")).willReturn(USERNAME);
            given(jwtUtils.isTokenValid("old")).willReturn(true);
            given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && !m.isEmpty())))
                    .willReturn("new-access");
            given(jwtUtils.generateToken(eq(USERNAME), argThat(m -> m != null && m.isEmpty())))
                    .willReturn("new-refresh");

            ResponseEntity<AuthenticationResponseDto> response = authService.refreshToken(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().accessToken()).isEqualTo("new-access");
            assertThat(response.getBody().refreshToken()).isEqualTo("new-refresh");
        }
    }
}