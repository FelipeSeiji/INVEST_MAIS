package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            // Desabilita o CSRF (necessário para APIs REST e para o Postman funcionar)
            .csrf(csrf -> csrf.disable()) 
            
            // Configura as permissões das rotas
            .authorizeHttpRequests(auth -> auth
                // Libera o POST para criar usuário (qualquer um pode acessar)
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll() 
                
                // Libera o console do banco de dados H2 e o Swagger/SpringDoc
                .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() 
                
                // Exige autenticação para qualquer outra requisição (ex: GET, PUT, DELETE)
                .anyRequest().authenticated() 
            )
            // Permite que o console do H2 abra corretamente no navegador
            .headers(headers -> headers.frameOptions(frame -> frame.disable())); 

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}