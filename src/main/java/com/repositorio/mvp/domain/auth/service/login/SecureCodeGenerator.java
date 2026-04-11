package com.repositorio.mvp.domain.auth.service.login;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.auth.service.interfaces.CodeGenerator;

@Component
public class SecureCodeGenerator implements CodeGenerator {
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Gera um código numérico aleatório de comprimento variável.
     * Utiliza SecureRandom para garantir que os códigos sejam criptograficamente fortes 
     * e imprevisíveis.
     * 
     * @param length Quantidade de dígitos no código gerado.
     * @return String formatada com o código numérico (incluindo zeros à esquerda se necessário).
     */
    @Override
    public String generate(int length) {
        return String.format(
            "%0" + length + "d", 
            secureRandom.nextInt(
                (int) Math.pow(10, length)
            )
        );
    }
}