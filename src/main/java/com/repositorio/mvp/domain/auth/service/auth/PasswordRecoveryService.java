package com.repositorio.mvp.domain.auth.service.auth;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.auth.model.PasswordResetToken;
import com.repositorio.mvp.domain.auth.repository.PasswordResetTokenRepository;
import com.repositorio.mvp.domain.auth.service.login.LoginAttemptService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;

import lombok.NonNull;
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

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final LoginAttemptService loginAttemptService;
    private final CryptoService cryptoService;
    private final RecoveryEmailService authEmailService;

    /**
     * Inicia o processo de recuperação de senha de forma segura.
     * Valida rate limit e delega a criação do token para um processo assíncrono
     * para mitigar ataques de enumeração de conta e tempo.
     * 
     * @param email E-mail para recuperação.
     * @param ip IP do solicitante para auditoria e rate limit.
     * @return ServiceResult<Void> Sempre retorna sucesso (mitigação).
     */
    public ServiceResult<Void> initiatePasswordRecovery(@NonNull String email, @NonNull String ip) {
        if (!loginAttemptService.isBlocked(ip)) {
            createPasswordResetTokenForUser(email);
        } else {
            log.warn(LogMessageConstants.SECURITY.PASSWORD_RECOVERY_BLOCKED_RATE_LIMIT, ip);
        }

        log.info(LogMessageConstants.AUTH.PASSWORD_RECOVERY_INITIATED, email, ip);

        // Previne Timing Attack: Equalizar tempo de resposta
        try {
            Thread.sleep(50);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }

        return ServiceResult.success(null);
    }

    /**
     * Cria um token de recuperação seguro e o associa ao e-mail do usuário.
     * O processo é disparado de forma assíncrona (@Async) para evitar que o tempo de 
     * processamento (e-mail, hashing) possa ser usado em ataques de tempo para 
     * validar a existência de contas.
     * 
     * @param email Endereço de e-mail fornecido pelo usuário para recuperação.
     */
    @Async
    public void createPasswordResetTokenForUser(@NonNull String email) {
        String emailHash = cryptoService.generateSha256Hash(email);

        userRepository.findBySecurityEmailHash(emailHash)
            .ifPresent(user -> {
                String token = cryptoService.generateSecureToken();
                String hashedToken = cryptoService.generateHmacTokenHash(token);
                
                transactionTemplate.execute(status -> {
                    PasswordResetToken myToken = new PasswordResetToken(hashedToken, "hmac-v1", user);
                    return passwordResetTokenRepository.save(myToken);
                });

                authEmailService.sendPasswordRecoveryEmail(email, user.getName(), token);
            });
    }

    /**
     * Valida o token de recuperação fornecido e redefine a senha do usuário.
     * O token é invalidado imediatamente após o uso bem-sucedido.
     * 
     * @param token String do token bruto enviado por e-mail.
     * @param newPassword Nova senha em texto puro a ser criptografada e armazenada.
     * @throws IllegalArgumentException Caso o token seja inválido ou já tenha expirado.
     */
    @Transactional
    public ServiceResult<Void> resetPassword(@NonNull String token, @NonNull String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByToken(cryptoService.generateHmacTokenHash(token))
            .orElse(null);

        if (resetToken == null) {
            return ServiceResult.error(MessageConstants.Auth.ERR_INVALID_TOKEN);
        }

        if (resetToken.getExpiryDate()
            .isBefore(LocalDateTime.now())) {
                passwordResetTokenRepository.delete(resetToken);
                return ServiceResult.error(MessageConstants.Auth.ERR_EXPIRED_TOKEN);
        }

        User user = resetToken.getUser();

        user.getSecurity().setPassword(passwordEncoder
            .encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        
        return ServiceResult.success(null);
    }
}