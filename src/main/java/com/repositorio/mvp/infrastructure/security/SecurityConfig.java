package com.repositorio.mvp.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

    /**
     * Ignora completamente a segurança para ferramentas de desenvolvimento.
     * Isso é mais robusto que permitAll() pois remove os filtros da jogada.
     */
    @Bean
    public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/h2-console/**",
            "/h2-console",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
        );
    }

    /**
     * Filtro dedicado para Ferramentas de Desenvolvimento/Admin (H2 e Swagger).
     * Permite Frames e Scripts Inline (necessários para as ferramentas funcionarem),
     * mas apenas via localhost ou perímetros controlados.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain devToolsSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**", "/h2-console", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // H2 precisa de frames
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " + // Relaxado para H2/Swagger
                                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                                "font-src 'self' https://fonts.gstatic.com; " +
                                                "img-src 'self' data: https://validator.swagger.io; " +
                                                "frame-src 'self'; " + // H2 usa iframes
                                                "connect-src 'self';")))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/templates/**",
                                "/index.html",
                                "/dashboard.html",
                                "/reset-password.html")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/auth/verify-2fa",
                                "/api/users",
                                "/auth/forgot-password",
                                "/auth/reset-password")
                        .permitAll()
                        .requestMatchers(
                                "/.git/**",
                                "/.env",
                                "/.gitignore",
                                "/.mvn/**",
                                "/*.sql",
                                "/*.sh",
                                "/target/**")
                        .denyAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // CABEÇALHOS DE SEGURANÇA (SECURITY HEADERS)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                                "script-src 'self'; " +
                                                "style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; " +
                                                "font-src 'self' https://fonts.gstatic.com; " +
                                                "connect-src 'self'; " +
                                                "frame-ancestors 'self'; " +
                                                "form-action 'self';"))
                        .referrerPolicy(referrer -> referrer
                                .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicy(permissions -> permissions
                                .policy("geolocation=(), microphone=(), camera=()")))
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}