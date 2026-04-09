package com.repositorio.mvp.domain.auth.service.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.auth.model.PasswordResetToken;
import com.repositorio.mvp.domain.auth.repository.PasswordResetTokenRepository;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por gerenciar o ciclo de vida da recuperação de contas.
 * Implementa defesas contra ataques de tempo (Timing Attacks) e geração criptográfica segura de tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private static final String MESSAGE_EMAIL_SUBJECT = "Recuperação de Senha - MVP";
    private static final String MESSAGE_EMAIL_BODY_TEMPLATE = "Olá %s,\n\nVocê solicitou a recuperação de senha.\nUtilize o token abaixo para redefinir sua senha:\n\n%s\n\nSe você não solicitou isso, ignore este e-mail.";
    private static final String MESSAGE_ERR_INVALID_TOKEN = "Token inválido ou não encontrado.";
    private static final String MESSAGE_ERR_EXPIRED_TOKEN = "Token expirado.";
    private static final String MESSAGE_ERR_HASH_EMAIL = "Erro ao gerar hash do e-mail para busca.";
    private static final String MESSAGE_ERR_HASH_TOKEN = "Erro ao gerar hash do token.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Cria um token de recuperação seguro e o associa ao e-mail do usuário.
     * O método é assíncrono (@Async) para mascarar o tempo de resposta da API.
     * @param email Endereço de e-mail em texto puro que o usuário digitou.
     */
    @Async
    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        String emailHash = generateEmailHash(email);

        userRepository.findBySecurityEmailHash(emailHash)
            .ifPresent(user -> {
                byte[] tokenBytes = new byte[32];
                secureRandom.nextBytes(tokenBytes);
                String token = Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
                PasswordResetToken myToken = new PasswordResetToken(
                    hashToken(token), 
                    user
                );

                passwordResetTokenRepository.save(myToken);

            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email); 
                message.setSubject(MESSAGE_EMAIL_SUBJECT);
                message.setText(
                    String.format(
                        MESSAGE_EMAIL_BODY_TEMPLATE, 
                        user.getName(), 
                        token
                    )
                );
                mailSender.send(message);
                log.info("E-mail de recuperação enviado para {}", 
                email
                );
            } catch (Exception e) {
                log.error("Erro ao enviar e-mail de recuperação", 
                e
                );
            }
        });
    }

    /**
     * Valida o token de recuperação fornecido e, se válido, altera a senha do usuário.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByToken(hashToken(token))
            .orElseThrow(() -> new IllegalArgumentException(
                MESSAGE_ERR_INVALID_TOKEN
            ));

        if (resetToken.getExpiryDate()
            .isBefore(LocalDateTime.now())) {
                passwordResetTokenRepository.delete(resetToken);
                throw new IllegalArgumentException(
                    MESSAGE_ERR_EXPIRED_TOKEN
                );
        }

        User user = resetToken.getUser();

        user.getSecurity().setPassword(passwordEncoder
            .encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    private String generateEmailHash(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                email.toLowerCase().getBytes(StandardCharsets.UTF_8)
            );
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(
                MESSAGE_ERR_HASH_EMAIL,
                e
            );
        }
    }

    /**
     * Gera o Hash Base64 do Token (usado internamente pela classe).
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                token.getBytes(StandardCharsets.UTF_8)
            );
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(
                MESSAGE_ERR_HASH_TOKEN,
                e
            );
        }
    }
}