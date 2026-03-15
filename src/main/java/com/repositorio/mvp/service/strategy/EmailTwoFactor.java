package com.repositorio.mvp.service.strategy;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.service.interfaces.TwoFactorNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logging Estruturado
@Component
@RequiredArgsConstructor
public class EmailTwoFactor implements TwoFactorNotification{
    private final JavaMailSender mailSender;

    @Override
    public void sendTwoFactorCode(User user, String code) {
        log.info("Iniciando envio de 2FA via E-mail para: {}", user.getEmail());
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Seu código de acesso (2FA) - MVP");
        message.setText("Olá " + user.getName() + ",\n\nSeu código de acesso é: " + code + "\nVálido por 5 minutos.");
        
        mailSender.send(message);
        log.info("E-mail de 2FA enviado com sucesso para: {}", user.getEmail());
    }
}