package com.repositorio.mvp.domain.auth.service.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
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
    private final JavaMailSender mailSender;
    private final TransactionTemplate transactionTemplate;
    private final LoginAttemptService loginAttemptService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${api.security.token.secret}")
    private String tokenSecret;

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

        // Mitigação de Timing Attack: Equalizar tempo de resposta
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
        String emailHash = generateEmailHash(email);

        userRepository.findBySecurityEmailHash(emailHash)
            .ifPresent(user -> {
                byte[] tokenBytes = new byte[32];
                secureRandom.nextBytes(tokenBytes);
                String token = Base64
                    .getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(tokenBytes);
                
                String hashedToken = hashToken(token);
                
                // Persistência em transação isolada
                transactionTemplate.execute(_ -> {
                    PasswordResetToken myToken = new PasswordResetToken(hashedToken, "hmac-v1", user);
                    return passwordResetTokenRepository.save(myToken);
                });

                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email); 
                    message.setSubject(MessageConstants.Auth.EMAIL_RECOVERY_SUBJECT);
                    message.setText(
                        String.format(
                            MessageConstants.Auth.EMAIL_RECOVERY_BODY, 
                            user.getName(), 
                            token
                        )
                    );
                    mailSender.send(message);
                    log.info(LogMessageConstants.AUTH.PASSWORD_RECOVERY_EMAIL_SENT, email);
                } catch (Exception e) {
                    log.error(LogMessageConstants.AUTH.PASSWORD_RECOVERY_EMAIL_ERROR, e);
                }
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
            .findByToken(hashToken(token))
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

    /**
     * Gera um hash determinístico do e-mail para permitir a busca no banco de dados
     * sem expor o e-mail original em texto puro.
     * 
     * @param email E-mail a ser hasheado.
     * @return Hash SHA-256 do e-mail em formato hexadecimal.
     * @throws RuntimeException Se houver falha na inicialização do algoritmo SHA-256.
     */
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
        } catch (Exception _) {
            throw new RuntimeException(
                MessageConstants.Auth.ERR_HASH_EMAIL);
        }
    }

    /**
     * Gera uma representação HMAC segura do token para armazenamento e comparação.
     * Utiliza a chave secreta do sistema para garantir que mesmo que o banco de 
     * dados seja comprometido, os tokens não possam ser revertidos ou forjados.
     * 
     * @param token Token bruto a ser hasheado.
     * @return Hash HMAC-SHA256 codificado em Base64Url.
     * @throws RuntimeException Se houver falha na geração do HMAC.
     */
    private String hashToken(String token) {
        try {
            javax.crypto.Mac sha256_HMAC = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(
                tokenSecret.getBytes(StandardCharsets.UTF_8), 
                "HmacSHA256"
            );
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(MessageConstants.Auth.ERR_HASH_TOKEN, e);
        }
    }
}