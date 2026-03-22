package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
            "/h2-console/**", 
            "/swagger-ui/**", 
            "/v3/api-docs/**", 
            "/swagger-ui.html"
        ).permitAll() 
        .requestMatchers(HttpMethod.POST, 
            "/auth/login", 
            "/auth/verify-2fa",
            "/api/users",
            "/auth/forgot-password",
            "/auth/reset-password"
        ).permitAll().anyRequest().authenticated() 
        )
        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
        .addFilterBefore(
            securityFilter,
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}