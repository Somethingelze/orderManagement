package com.some.inventoryservice.configuration;

import com.some.commonlib.jwt.JwtUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtUtils jwtUtils(JwtProperties properties) {
        return new JwtUtils(properties.getSecretKey());
    }
}