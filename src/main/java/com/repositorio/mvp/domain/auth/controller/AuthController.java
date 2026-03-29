package com.repositorio.mvp.domain.auth.controller;

import com.repositorio.mvp.domain.auth.service.login.LoginAttemptService;
import com.repositorio.mvp.domain.auth.service.login.LoginService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.ForgotPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.ResetPasswordRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.TokenResponseDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;
import com.repositorio.mvp.domain.auth.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.domain.auth.service.auth.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador responsável por gerenciar todo o fluxo de autenticação pública da API.
 * Lida com login, verificação de dois fatores (2FA), logout e recuperação de senhas.
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
     * Inicia o processo de login do usuário.
     * Valida as credenciais e, em caso de sucesso, gera um código 2FA e envia por e-mail.
     * * @param loginRequest DTO contendo e-mail e senha do usuário.
     * @param request Objeto HTTP da requisição usado para extrair o IP do cliente (para proteção contra força bruta).
     * @return Mensagem de sucesso indicando o envio do e-mail.
     */
    // POST /auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    public MessageResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        loginService.initiateLogin(loginRequest, ip);

        return new MessageResponseDTO("Código de verificação enviado para o seu e-mail.");
    }

    /**
     * Conclui o processo de login verificando o código de dois fatores (2FA).
     * Se o código for válido e não estiver expirado, libera o token JWT.
     * * @param verifyRequest DTO contendo o e-mail e o código numérico recebido.
     * @param request Objeto HTTP usado para extrair o IP e validar limites de tentativas.
     * @return Token JWT para autenticação nas rotas protegidas.
     */
    // POST /auth/verify-2fa
    @PostMapping("/verify-2fa")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    public TokenResponseDTO verify2FA(@Valid @RequestBody Verify2FARequestDTO verifyRequest, HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        String token = loginService.verify2FAAndGenerateToken(verifyRequest, ip);
        return new TokenResponseDTO(token);
    }

    /**
     * Encerra a sessão ativa do usuário invalidando o token JWT atual.
     * O token é adicionado a uma Blacklist no banco de dados para impedir reuso.
     * * @param token Token JWT enviado no cabeçalho Authorization.
     */
    // POST /auth/logout
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT fornecido no cabeçalho de autorização, encerrando a sessão ativa.")
    public void logout(@RequestHeader("Authorization") String token) {
        SecurityContextHolder.clearContext();
        sessionService.logout(token);  
    }

    /**
     * Solicita o envio de um link de recuperação de senha para o e-mail informado.
     * * @param request DTO contendo o e-mail do usuário no corpo da requisição.
     * @return Mensagem genérica de sucesso (evita vazamento de informações sobre quais e-mails existem no sistema).
     */
    // POST /auth/forgot-password
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Solicita a recuperação de senha", description = "Gera um token de redefinição e envia um link para o e-mail informado.")
    public MessageResponseDTO forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO, HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);

        if(loginAttemptService.isBlocked(ip)) {
            return new MessageResponseDTO("Muitas requisições. Tente novamente mais tarde.");
        }
        
        passwordRecoveryService.createPasswordResetTokenForUser(forgotPasswordRequestDTO.email());
        
        log.info("RECUPERAÇÃO DE SENHA: Solicitação iniciada para o e-mail: {} a partir do IP: {}", forgotPasswordRequestDTO.email(), ip);
        
        return new MessageResponseDTO("Se o e-mail existir, um link de recuperação foi enviado.");
    }

    /**
     * Efetiva a troca de senha utilizando um token válido gerado no processo de recuperação.
     * * @param request DTO contendo o token de recuperação e a nova senha desejada.
     * @return Mensagem confirmando a alteração da senha.
     */
    // POST /auth/reset-password
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Redefine a senha do usuário", description = "Recebe o token de recuperação e a nova senha para atualizar as credenciais.")
    public MessageResponseDTO resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request, HttpServletRequest httpRequest) {
        String ip = ClientIp.getClientIp(httpRequest);

        passwordRecoveryService.resetPassword(request.token(), request.newPassword());

        log.info("ALERTA DE SEGURANÇA: Senha redefinida com sucesso via token. IP de origem: {}", ip);

        return new MessageResponseDTO("Senha redefinida com sucesso.");
    }
}