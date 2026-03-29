package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .requiresChannel(channel -> channel.anyRequest().requiresSecure()) 
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
                ).permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated() 
            )
            // CABEÇALHOS DE SEGURANÇA (SECURITY HEADERS)
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny())
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)) // Força navegadores a usarem apenas HTTPS por 1 ano
                .contentTypeOptions(contentType -> contentType.disable()) // Impede ataques MIME-sniffing
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}