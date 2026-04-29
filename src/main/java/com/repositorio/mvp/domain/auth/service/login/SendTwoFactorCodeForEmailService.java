package com.repositorio.mvp.domain.auth.service.login;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.auth.service.interfaces.TwoFactorNotificationService;
import com.repositorio.mvp.domain.user.model.User;

import lombok.RequiredArgsConstructor;

/**
 * Implementação da estratégia de notificação de Dois Fatores (2FA) utilizando E-mail.
 */
@Component
@RequiredArgsConstructor
public class SendTwoFactorCodeForEmailService implements TwoFactorNotificationService{
    private final JavaMailSender mailSender;

    /**
     * Prepara e envia o e-mail contendo o código de segurança 2FA para o usuário.
     * Utiliza o JavaMailSender para o disparo de mensagens simples de texto.
     * 
     * @param user Entidade do usuário que solicitou o acesso.
     * @param code Código numérico de 6 dígitos gerado para validação.
     */
    @Override
    public void sendTwoFactorCode(User user, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setTo(user.getEmail());
        message.setSubject("Seu código de acesso (2FA) - MVP");
        message.setText("Olá " + user.getName() + ",\n\nSeu código de acesso é: " + code + "\nVálido por 5 minutos.");
        
        mailSender.send(message);
    }
    
}