package com.repositorio.mvp.domain.auth.login.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.auth.login.service.TwoFactorNotificationService;
import com.repositorio.mvp.domain.user.model.User;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar a geração e o envio de códigos de Segundo Fator (2FA).
 * Centraliza a lógica de expiração e integração com provedores de notificação.
 */
@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final CryptoService cryptoService;
    private final TwoFactorNotificationService twoFactorNotification;
    
    /**
     * Prepara o processo de segundo fator de autenticação (2FA) para um usuário.
     * Gera um código numérico de 6 dígitos e define um prazo de expiração de 5 minutos.
     * 
     * @param user Objeto do usuário que está tentando se autenticar.
     */
    public void prepareAndSendTwoFactor(@NonNull User user) {
        String code = cryptoService.generateNumericCode(6);
        user.getSecurity().generateTwoFactorCode(
            code, 
            LocalDateTime.now()
                .plusMinutes(5)
        );
        twoFactorNotification.sendTwoFactorCode(user, code);
    }
}