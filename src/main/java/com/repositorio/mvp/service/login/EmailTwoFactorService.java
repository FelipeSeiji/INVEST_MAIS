package com.repositorio.mvp.service.login;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.service.interfaces.TwoFactorNotification;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailTwoFactorService implements TwoFactorNotification{
    private final JavaMailSender mailSender;

    @Override
    public void sendTwoFactorCode(User user, String code) {
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Seu código de acesso (2FA) - MVP");
        message.setText("Olá " + user.getName() + ",\n\nSeu código de acesso é: " + code + "\nVálido por 5 minutos.");
        
        mailSender.send(message);
    }
}