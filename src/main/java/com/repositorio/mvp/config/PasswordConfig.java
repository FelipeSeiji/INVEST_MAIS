package com.repositorio.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração dos algoritmos de criptografia do sistema.
 */
@Configuration
public class PasswordConfig {
    /**
     * Fornece o Bean responsável por encriptar (hash) as senhas.
     * Utiliza o BCrypt, o padrão da indústria, configurado com força de processamento (work factor) 12,
     * balanceando segurança contra ataques de força bruta e desempenho do servidor.
     * @return Instância do BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}