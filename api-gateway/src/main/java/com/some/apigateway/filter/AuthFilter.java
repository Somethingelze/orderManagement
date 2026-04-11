package com.some.apigateway.filter;

import com.some.commonlib.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final JwtUtils jwtUtils;

    public AuthFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Unauthorized", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            if (!jwtUtils.isTokenValid(token)) {
                return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            try {
                Claims claims = jwtUtils.extractClaims(token);

                if (config.getRequiredRole() != null) {
                    Object rolesObject = claims.get("roles");
                    boolean hasRole = false;

                    if (rolesObject instanceof List<?> list) {
                        hasRole = list.stream()
                                .filter(String.class::isInstance)
                                .map(String.class::cast)
                                .anyMatch(role -> role.equals(config.getRequiredRole()));
                    }

                    if (!hasRole) {
                        return onError(exchange, "Forbidden: Role " + config.getRequiredRole() + " required", HttpStatus.FORBIDDEN);
                    }
                }

                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", claims.getSubject())
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                log.error("JWT Error: {}", e.getMessage());
                return onError(exchange, "Token Processing Error", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        log.error("Auth Error: {}", err);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Data
    public static class Config {
        private String requiredRole;
    }
}