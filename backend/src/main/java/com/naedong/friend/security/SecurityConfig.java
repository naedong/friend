package com.naedong.friend.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Profile({"dev", "test"})
    @ConditionalOnProperty(name = "friend.security.dev-actor-enabled", havingValue = "true")
    SecurityFilterChain developmentSecurityFilterChain(HttpSecurity http) throws Exception {
        // TODO SECURITY: Replace this dev-only permissive chain with authenticated principal integration.
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
