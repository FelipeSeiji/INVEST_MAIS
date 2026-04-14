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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável por gerenciar todo o fluxo de autenticação pública da
 * API.
 * Lida com login, verificação de dois fatores (2FA), logout e recuperação de
 * senhas.
 */

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginAttemptService loginAttemptService;
    private final LoginService loginService;
    private final SessionService sessionService;
    private final PasswordRecoveryService passwordRecoveryService;

    /**
     * Inicia o processo de login do usuário (Fase 1).
     * Valida as credenciais (e-mail e senha) e, se corretas, dispara o envio de um
     * código de verificação 2FA para o e-mail do usuário.
     * 
     * @param loginRequest DTO contendo e-mail e senha para autenticação base.
     * @param request      Objeto HttpServletRequest para captura do IP do cliente e
     *                     auditoria.
     * @return Mensagem confirmando que o código de verificação foi encaminhado.
     * @throws IllegalArgumentException Caso as credenciais sejam inválidas ou o
     *                                  acesso esteja bloqueado.
     */
    // POST /auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    public MessageResponseDTO login(@Valid @RequestBody @NonNull LoginRequestDTO loginRequest, @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        loginService.initiateLogin(loginRequest, ip);

        return new MessageResponseDTO(MessageConstants.Auth.LOGIN_2FA_SENT);
    }

    /**
     * Conclui o processo de login (Fase 2) validando o código 2FA.
     * Emite um token JWT de longa duração caso a validação seja bem-sucedida.
     * 
     * @param verifyRequest DTO contendo o código de 6 dígitos e o e-mail do
     *                      usuário.
     * @param request       Objeto HttpServletRequest para controle de segurança e
     *                      auditoria.
     * @return DTO contendo o Token JWT assinado para sessões futuras.
     * @throws IllegalArgumentException Se o código for inválido ou estiver
     *                                  expirado.
     */
    // POST /auth/verify-2fa
    @PostMapping("/verify-2fa")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    public TokenResponseDTO verify2FA(@Valid @RequestBody @NonNull Verify2FARequestDTO verifyRequest,
            @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        String token = loginService.verify2FAAndGenerateToken(
                verifyRequest,
                ip);

        return new TokenResponseDTO(token);
    }

    /**
     * Realiza o logout do usuário autenticado.
     * Invalida o token JWT no lado do servidor através da adição em uma Blacklist.
     * 
     * @param token O token de autorização extraído do cabeçalho "Authorization".
     */
    // POST /auth/logout
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT fornecido no cabeçalho de autorização, encerrando a sessão ativa.")
    public void logout(@RequestHeader("Authorization") @NonNull String token) {
        SecurityContextHolder.clearContext();
        sessionService.logout(token);
    }

    /**
     * Inicia o fluxo de recuperação de conta para senhas esquecidas.
     * Emite um token de redefinição com link seguro via e-mail.
     * 
     * @param forgotPasswordRequestDTO DTO contendo o e-mail do usuário alvo.
     * @param request                  Objeto HttpServletRequest para auditoria e
     *                                 controle de abuso.
     * @return Mensagem amigável de sucesso (sem revelar existência da conta).
     */
    // POST /auth/forgot-password
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Solicita a recuperação de senha", description = "Gera um token de redefinição e envia um link para o e-mail informado.")
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

    /**
     * Redefine a senha do usuário utilizando um token de recuperação.
     * 
     * @param request     DTO com o token bruto e a nova senha desejada.
     * @param httpRequest Objeto HttpServletRequest para auditoria de origem.
     * @return Mensagem confirmando a alteração da credencial.
     * @throws IllegalArgumentException Caso o token seja inválido ou já tenha
     *                                  expirado.
     */
    // POST /auth/reset-password
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Redefine a senha do usuário", description = "Recebe o token de recuperação e a nova senha para atualizar as credenciais.")
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