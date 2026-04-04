package com.some.orderservice.configuration;


import com.some.orderservice.filters.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Для REST API отключаем
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT = Stateless
                .authorizeHttpRequests(auth -> auth
                        // 1. Публичные эндпоинты (Авторизация и Документация)
                        .requestMatchers("/auth/reg", "/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers("/", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 2. Управление пользователями (CRUD users — обычно только для ADMIN)
                        // Если в ТЗ не указано иное, создание/удаление юзеров закрываем админкой
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("ADMIN")

                        // 3. Заказы (Бизнес-процесс)
                        // Создать заказ может любой авторизованный пользователь (ROLE_USER или ROLE_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/orders**").hasAnyAuthority("USER", "ADMIN")

                        // 4. Доступ к данным в других сервисах (Аналитика в Notification Service)
                        // Эти эндпоинты по ТЗ находятся в Notification Service,
                        // но если ты проксируешь их через Order Service:
                        .requestMatchers("/api/orders/all").hasAuthority("ADMIN") // Вся таблица заказов
                        .requestMatchers(HttpMethod.POST, "/api/orders/create").authenticated()
                        .requestMatchers("/api/orders/products/{id}").authenticated() // По конкретному заказу
                        .requestMatchers("/api/orders/users/{id}").authenticated()  // По конкретному юзеру

                        // 5. Все остальное должно быть защищено
                        .anyRequest().authenticated()
                )
                // Твой фильтр JWT
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(log -> log
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                SecurityContextHolder.clearContext())
                );

        return http.build();
    }
}
