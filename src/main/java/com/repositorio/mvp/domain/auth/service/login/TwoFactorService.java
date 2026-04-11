package com.repositorio.mvp.domain.auth.service.login;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.domain.auth.service.interfaces.CodeGenerator;
import com.repositorio.mvp.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final CodeGenerator codeGenerator;
    
    /**
     * Prepara o processo de segundo fator de autenticação (2FA) para um usuário.
     * Gera um código numérico de 6 dígitos e define um prazo de expiração de 5 minutos.
     * 
     * @param user Objeto do usuário que está tentando se autenticar.
     */
    public void prepareTwoFactor(User user) {
        String code = codeGenerator.generate(6);
        user.getSecurity().generateTwoFactorCode(
            code, 
            LocalDateTime.now()
                .plusMinutes(5)
        );
    }
}