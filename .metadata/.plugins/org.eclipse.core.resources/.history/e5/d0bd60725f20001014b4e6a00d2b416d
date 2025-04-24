package com.example.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class ResourceServerConfig {

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.authorizeExchange().anyExchange().authenticated();
        httpSecurity.oauth2ResourceServer(c -> c.jwt(j -> j.jwkSetUri("http://auth-server:9000/oauth2/jwks")));

        return httpSecurity.build();
    }
}
