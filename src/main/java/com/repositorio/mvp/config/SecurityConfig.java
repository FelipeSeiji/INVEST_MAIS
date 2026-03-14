package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            // 1. Libere o endpoint de LOGIN (geralmente /auth/login ou similar)
            .requestMatchers(HttpMethod.POST, "/auth/login").permitAll() 
            
            // 2. Libere o endpoint de CADASTRO (que você já tinha)
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll() 
            
            // 3. Libere recursos de infra/docs
            .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() 
            
            // O resto exige token JWT
            .anyRequest().authenticated() 
        )
        // O H2 usa frames, por isso o disable ou sameOrigin é necessário
        .headers(headers -> headers.frameOptions(frame -> frame.disable())); 

    return http.build();
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}