package com.some.authservice.configuration;

import com.some.commonlib.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${security.jwt.secret_key}")
    private String secret;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(secret);
    }
}