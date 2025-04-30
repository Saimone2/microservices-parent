package com.sa1mone.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AddRolesGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            List<String> roles = extractRolesFromToken(token);
            String userEmail = extractUserEmailFromToken(token);

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .headers(headers -> {
                                headers.add("X-Roles", String.join(",", roles));
                                headers.add("X-User-Email", userEmail);
                            })
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String extractUserEmailFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("email").asString();
    }

    private List<String> extractRolesFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access").asMap();
        if (resourceAccess != null && resourceAccess.containsKey("auth-service")) {
            Object authServiceObject = resourceAccess.get("auth-service");
            if (authServiceObject instanceof Map<?, ?> authServiceMap) {
                Object rolesObject = authServiceMap.get("roles");
                if (rolesObject instanceof List<?> rawRoles) {
                    List<String> roles = new ArrayList<>();
                    for (Object role : rawRoles) {
                        if (role instanceof String) {
                            roles.add((String) role);
                        }
                    }
                    return roles;
                }
            }
        }
        return Collections.emptyList();
    }
}