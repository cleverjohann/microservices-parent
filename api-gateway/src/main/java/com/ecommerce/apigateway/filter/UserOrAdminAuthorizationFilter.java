package com.ecommerce.apigateway.filter;

import com.ecommerce.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserOrAdminAuthorizationFilter extends AbstractGatewayFilterFactory<UserOrAdminAuthorizationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public UserOrAdminAuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Verificar si tiene header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validar el token
                if (!jwtUtil.isTokenValid(token)) {
                    return onError(exchange, "Token is invalid", HttpStatus.UNAUTHORIZED);
                }

                // Verificar que tenga rol USER o ADMIN
                if (!jwtUtil.hasRole(token, "USER") && !jwtUtil.hasRole(token, "ADMIN")) {
                    return onError(exchange, "Access denied. User or Admin role required", HttpStatus.FORBIDDEN);
                }

                // Extraer información del usuario y agregarla a los headers
                String userId = jwtUtil.extractUserId(token);
                String userEmail = jwtUtil.extractUsername(token);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Email", userEmail)
                        .header("X-User-Roles", String.join(",", jwtUtil.extractRoles(token)))
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                return onError(exchange, "Token validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format("{\"error\": \"%s\", \"status\": %d}", message, httpStatus.value());

        return response.writeWith(Mono.fromSupplier(() -> {
            return response.bufferFactory().wrap(body.getBytes());
        }));
    }

    public static class Config {
        // Configuración del filtro si necesitas parámetros
    }
}