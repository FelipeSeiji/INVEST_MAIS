package com.repositorio.mvp.domain.auth.service.login;

import java.time.LocalDateTime;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.auth.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.domain.auth.service.token.TokenProvider;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por orquestrar a lógica de negócio da autenticação.
 * Garante a verificação segura de senhas, controle de ataques de força bruta e
 * gestão do ciclo de vida do 2FA.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private static final String MESSAGE_PREFIX_2FA_ATTEMPT = "2FA:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TwoFactorService twoFactorService;
    private final TwoFactorNotification twoFactorStrategy;
    private final LoginAttemptService loginAttemptService;

    /**
     * Processa a primeira etapa do login.
     * Verifica se o IP ou a conta estão bloqueados por excesso de tentativas,
     * valida a senha e aciona o envio do código de 2FA para o usuário.
     * 
     * @param loginRequest Objeto contendo as credenciais de acesso (e-mail e senha).
     * @param ip Endereço IP do cliente requisitante para controle de segurança.
     * @throws IllegalArgumentException Caso as credenciais sejam inválidas ou a tentativa seja bloqueada por segurança.
     */
    @Transactional
    public void initiateLogin(@NonNull LoginRequestDTO loginRequest, @NonNull String ip) {
        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked(loginRequest.email())) {
            log.warn("ALERTA: Tentativa de login bloqueada (Força Bruta). IP: {} | Conta alvo: {}", 
                ip, 
                loginRequest.email());
            throw new IllegalArgumentException(MessageConstants.Auth.ERR_TOO_MANY_ATTEMPTS);
        }

        String searchHash = DigestUtils
            .sha256Hex(
                loginRequest.email()
                    .toLowerCase()
            );
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(ip);
                    log.warn("FALHA DE LOGIN: Usuário não existe. IP: {} | E-mail tentado: {}", 
                        ip, 
                        maskEmail(loginRequest.email())
                    );
                    return new IllegalArgumentException(MessageConstants.Auth.ERR_INVALID_CREDENTIALS);
                });

        if (!passwordEncoder.matches(loginRequest.password(), 
            user.getSecurity()
                .getPassword())) {
                loginAttemptService.loginFailed(ip);
                loginAttemptService.loginFailed(user.getEmail());
                log.warn("FALHA DE LOGIN: Senha incorreta. IP: {} | E-mail: {}", 
                    ip, 
                    maskEmail(user.getEmail()));
                throw new IllegalArgumentException(MessageConstants.Auth.ERR_INVALID_CREDENTIALS);
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(user.getEmail());

        log.info("LOGIN FASE 1: Credenciais válidas. Gerando 2FA para o usuário {}. IP: {}", 
            user.getId(), 
            ip
        );

        twoFactorService.prepareTwoFactor(user);
        userRepository.save(user);

        twoFactorStrategy.sendTwoFactorCode(
            user, 
            user.getSecurity()
                .getTwoFactorCode()
        );
    }

    /**
     * Processa a segunda etapa do login através da verificação do código 2FA.
     * Caso o código seja válido e esteja dentro do prazo de expiração, um token JWT é gerado.
     * 
     * @param verifyRequest Objeto contendo o e-mail e o código 2FA informado pelo usuário.
     * @param ip Endereço IP do cliente requisitante para controle de segurança.
     * @return String contendo o Token JWT assinado para autenticação nas próximas requisições.
     * @throws IllegalArgumentException Caso o código seja inválido, expirado ou a conta esteja bloqueada.
     */
    @Transactional
    public String verify2FAAndGenerateToken(@NonNull Verify2FARequestDTO verifyRequest, @NonNull String ip) {
        String attemptKey = MESSAGE_PREFIX_2FA_ATTEMPT + verifyRequest.email();

        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked(attemptKey)) {
            log.warn("ALERTA: Tentativa de 2FA bloqueada (Força Bruta). IP: {} | Conta alvo: {}", 
                ip, 
                verifyRequest.email());
            throw new IllegalArgumentException(MessageConstants.Auth.ERR_TOO_MANY_ATTEMPTS_2FA);
        }

        String searchHash = DigestUtils.sha256Hex(verifyRequest.email()
            .toLowerCase());
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElseThrow(() -> new IllegalArgumentException(MessageConstants.User.NOT_FOUND));

        if (user.getSecurity().getTwoFactorCode() == null || 
            !user.getSecurity()
                .getTwoFactorCode()
                .equals(verifyRequest.code())) {
                    loginAttemptService.loginFailed(ip);
                    loginAttemptService.loginFailed(attemptKey);
                    log.warn("FALHA 2FA: Código inválido. IP: {} | E-mail: {}", 
                        ip, 
                        maskEmail(user.getEmail()));
                    throw new IllegalArgumentException(MessageConstants.Auth.ERR_INVALID_2FA);
        }

        if (user.getSecurity()
            .getTwoFactorExpiry()
            .isBefore(LocalDateTime.now())) {
                user.getSecurity().clearTwoFactorCode();
                userRepository.save(user);
                log.warn("FALHA 2FA: Código expirado. IP: {} | E-mail: {}", ip, user.getEmail());
                throw new IllegalArgumentException(MessageConstants.Auth.ERR_EXPIRED_2FA);
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(attemptKey);
        user.getSecurity().clearTwoFactorCode();
        userRepository.save(user);

        log.info("LOGIN SUCESSO: 2FA validado. Token JWT emitido para o usuário {}. IP: {}", user.getId(), ip);

        return tokenProvider.generateToken(user.getId());
    }

    /**
     * Ofusca o endereço de e-mail para exibição segura em logs de auditoria.
     * Mantém os primeiros caracteres e o domínio, ocultando a parte central.
     * 
     * @param email Endereço de e-mail original completo.
     * @return String contendo o e-mail mascarado (ex: jo***@domain.com).
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return "***";
        String[] parts = email.split("@");
        return parts[0]
            .substring(0, 
                Math.min(
                    2, 
                    parts[0].length()
                )
            ) + "***@" + parts[1];
    }
}