package com.repositorio.mvp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração dos algoritmos de criptografia do sistema.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        int saltLength = 16;
        int hashLength = 32; 
        int parallelism = 1; 
        int memory = 16384;
        int iterations = 2;

        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }
}