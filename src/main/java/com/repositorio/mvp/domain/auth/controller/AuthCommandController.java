package com.repositorio.mvp.domain.auth.controller;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.auth.DTO.ForgotPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.ResetPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.TokenResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.auth.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.domain.auth.service.auth.SessionService;
import com.repositorio.mvp.domain.auth.service.login.LoginAttemptService;
import com.repositorio.mvp.domain.auth.service.login.LoginService;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;
import com.repositorio.mvp.common.DTO.MessageResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Commands", description = "Endpoints públicos para login, 2FA e recuperação de senhas")
public class AuthCommandController {
    private final LoginAttemptService loginAttemptService;
    private final LoginService loginService;
    private final SessionService sessionService;
    private final PasswordRecoveryService passwordRecoveryService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    @ApiResponse(responseCode = "200", description = "Código 2FA enviado com sucesso")
    public MessageResponseDTO login(@Valid @RequestBody @NonNull LoginRequestDTO loginRequest, @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        loginService.initiateLogin(loginRequest, ip);

        return new MessageResponseDTO(MessageConstants.Auth.LOGIN_2FA_SENT);
    }

    @PostMapping("/verify-2fa")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    @ApiResponse(responseCode = "200", description = "Token JWT gerado com sucesso")
    public TokenResponseDTO verify2FA(@Valid @RequestBody @NonNull Verify2FARequestDTO verifyRequest,
            @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        String token = loginService.verify2FAAndGenerateToken(
                verifyRequest,
                ip);

        return new TokenResponseDTO(token);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT fornecido no cabeçalho de autorização, encerrando a sessão ativa.")
    @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso")
    public void logout(@RequestHeader("Authorization") @NonNull String token) {
        SecurityContextHolder.clearContext();
        sessionService.logout(token);
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Solicita a recuperação de senha", description = "Gera um token de redefinição e envia um link para o e-mail informado.")
    @ApiResponse(responseCode = "200", description = "Token de recuperação enviado com sucesso")
    public MessageResponseDTO forgotPassword(@Valid @RequestBody @NonNull ForgotPasswordRequestDTO forgotPasswordRequestDTO,
            @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);

        if (!loginAttemptService.isBlocked(ip)) {
            passwordRecoveryService.createPasswordResetTokenForUser(forgotPasswordRequestDTO.email());
        } else {
            log.warn("RECUPERAÇÃO DE SENHA BLOQUEADA (Rate Limit): IP {} está bloqueado.", ip);
        }

        log.info("RECUPERAÇÃO DE SENHA: Solicitação iniciada para o e-mail: {} a partir do IP: {}",
                forgotPasswordRequestDTO.email(), ip);

        return new MessageResponseDTO(MessageConstants.Auth.FORGOT_PASSWORD_SENT);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Redefine a senha do usuário", description = "Recebe o token de recuperação e a nova senha para atualizar as credenciais.")
    @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso")
    public MessageResponseDTO resetPassword(@Valid @RequestBody @NonNull ResetPasswordRequestDTO request,
            @NonNull HttpServletRequest httpRequest) {
        String ip = ClientIp.getClientIp(httpRequest);

        passwordRecoveryService.resetPassword(
                request.token(),
                request.newPassword());

        log.info("ALERTA DE SEGURANÇA: Senha redefinida com sucesso via token. IP de origem: {}", ip);

        return new MessageResponseDTO(MessageConstants.Auth.PASSWORD_RESET_SUCCESS);
    }
}
