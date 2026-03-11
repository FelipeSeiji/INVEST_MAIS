package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //remove o form de login e auth, autorizar o h2
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );

        return http.build();
    }
    
    @Bean
    //hash com salt
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(14);
    }
}