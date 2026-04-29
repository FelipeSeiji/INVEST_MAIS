package com.repositorio.mvp.domain.auth.service.login;

import java.time.LocalDateTime;

import com.repositorio.mvp.common.security.CryptoService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.auth.service.interfaces.TokenProviderService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProviderService tokenProvider;
    private final TwoFactorService twoFactorService;
    private final LoginAttemptService loginAttemptService;
    private final CryptoService cryptoService;

    /**
     * Processa a primeira etapa do login.
     * Verifica se o IP ou a conta estão bloqueados por excesso de tentativas,
     * valida a senha e aciona o envio do código de 2FA para o usuário.
     * 
     * @param loginRequest Objeto contendo as credenciais de acesso (e-mail e senha).
     * @param ip Endereço IP do cliente requisitante para controle de segurança.
     * @return ServiceResult<Void> Sucesso caso as credenciais sejam válidas e o 2FA enviado.
     */
    @Transactional
    public ServiceResult<Void> initiateLogin(@NonNull LoginRequestDTO loginRequest, @NonNull String ip) {
        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked(loginRequest.email())) {
            log.warn(LogMessageConstants.SECURITY.BRUTE_FORCE_LOGIN_BLOCKED, 
                ip, 
                loginRequest.email());
            return ServiceResult.error(MessageConstants.Auth.ERR_TOO_MANY_ATTEMPTS);
        }

        String searchHash = cryptoService.generateSha256Hash(loginRequest.email());
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElse(null);

        if (user == null) {
            loginAttemptService.loginFailed(ip);
            log.warn(LogMessageConstants.AUTH.LOGIN_FAILED_USER_NOT_FOUND, 
                ip, 
                maskEmail(loginRequest.email())
            );
            return ServiceResult.error(MessageConstants.Auth.ERR_INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequest.password(), 
            user.getSecurity()
                .getPassword())) {
                loginAttemptService.loginFailed(ip);
                loginAttemptService.loginFailed(user.getEmail());
                log.warn(LogMessageConstants.AUTH.LOGIN_FAILED_INVALID_PASSWORD, 
                    ip, 
                    maskEmail(user.getEmail()));
                return ServiceResult.error(MessageConstants.Auth.ERR_INVALID_CREDENTIALS);
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(user.getEmail());

        log.info(LogMessageConstants.AUTH.LOGIN_INITIATED, 
            user.getId(), 
            ip
        );

        twoFactorService.prepareAndSendTwoFactor(user);
        userRepository.save(user);

        return ServiceResult.success(null);
    }

    /**
     * Processa a segunda etapa do login através da verificação do código 2FA.
     * Caso o código seja válido e esteja dentro do prazo de expiração, um token JWT é gerado.
     * 
     * @param verifyRequest Objeto contendo o e-mail e o código 2FA informado pelo usuário.
     * @param ip Endereço IP do cliente requisitante para controle de segurança.
     * @return ServiceResult<String> Token JWT assinado em caso de sucesso.
     */
    @Transactional
    public ServiceResult<String> verify2FAAndGenerateToken(@NonNull Verify2FARequestDTO verifyRequest, @NonNull String ip) {
        String attemptKey = MessageConstants.Auth.PREFIX_2FA + verifyRequest.email();

        if (loginAttemptService.isBlocked(ip)) {
            log.warn(LogMessageConstants.SECURITY.BRUTE_FORCE_2FA_BLOCKED, 
                ip, 
                verifyRequest.email());
            return ServiceResult.error(MessageConstants.Auth.ERR_TOO_MANY_ATTEMPTS_2FA);
        }

        String searchHash = cryptoService.generateSha256Hash(verifyRequest.email());
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElse(null);

        if (user == null) {
            return ServiceResult.notFound(MessageConstants.User.NOT_FOUND);
        }

        if (user.getSecurity().getTwoFactorCode() == null || 
            !user.getSecurity()
                .getTwoFactorCode()
                .equals(verifyRequest.code())) {
                    loginAttemptService.loginFailed(ip);
                    log.warn(LogMessageConstants.AUTH.LOGIN_2FA_FAILED_INVALID_CODE, 
                        ip, 
                        maskEmail(user.getEmail()));
                    return ServiceResult.error(MessageConstants.Auth.ERR_INVALID_2FA);
        }

        if (user.getSecurity()
            .getTwoFactorExpiry()
            .isBefore(LocalDateTime.now())) {
                user.getSecurity().clearTwoFactorCode();
                userRepository.save(user);
                log.warn(LogMessageConstants.AUTH.LOGIN_2FA_FAILED_EXPIRED_CODE, ip, user.getEmail());
                return ServiceResult.error(MessageConstants.Auth.ERR_EXPIRED_2FA);
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(attemptKey);
        user.getSecurity().clearTwoFactorCode();
        userRepository.save(user);

        log.info(LogMessageConstants.AUTH.LOGIN_SUCCESS, user.getId(), ip);

        return ServiceResult.success(tokenProvider.generateToken(user.getId()));
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