package com.repositorio.mvp.domain.auth.service.interfaces;

import com.repositorio.mvp.domain.user.model.User;

/**
 * Interface que define o contrato para envio de notificações de Segundo Fator (2FA).
 * Permite diferentes estratégias de envio, como e-mail, SMS ou push notifications.
 */
public interface TwoFactorNotificationService {
    /**
     * Envia o código de segurança para o usuário através do canal implementado.
     * 
     * @param user Usuário destinatário da notificação.
     * @param code Código de segurança gerado.
     */
    void sendTwoFactorCode(User user, String code);
}
