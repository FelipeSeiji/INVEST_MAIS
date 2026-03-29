package com.repositorio.mvp.domain.auth.service.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.auth.model.PasswordResetToken;
import com.repositorio.mvp.domain.auth.repository.PasswordResetTokenRepository;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar o ciclo de vida da recuperação de contas.
 * Implementa defesas contra ataques de tempo (Timing Attacks) e geração criptográfica segura de tokens.
 */

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Cria um token de recuperação seguro e o associa ao e-mail do usuário.
     * O método é assíncrono (@Async) para mascarar o tempo de resposta da API,
     * impedindo que hackers descubram quais e-mails estão cadastrados na plataforma.
     * @param email Endereço de e-mail do usuário que solicitou a recuperação.
     */
    @Async
    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.save(myToken);
            
            // TODO: Integrar com uma estratégia de envio de e-mail (ex: JavaMailSender, SendGrid, AWS SES)
            // para enviar o link com o token para o usuário.
        });
    }

    /**
     * Valida o token de recuperação fornecido e, se válido, altera a senha do usuário.
     * O token é de uso único e tem prazo de validade rígido.
     * @param token Hash de verificação recebido pelo usuário via e-mail.
     * @param newPassword Nova senha desejada pelo usuário (já validada pelos DTOs).
     * @throws IllegalArgumentException Se o token for inexistente ou estiver expirado.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou não encontrado."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expirado.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(resetToken);
    }
}