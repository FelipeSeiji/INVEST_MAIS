package com.repositorio.mvp.domain.auth.controller;

import com.repositorio.mvp.common.constants.LogMessageConstants;
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
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import com.repositorio.mvp.infrastructure.exception.RateLimitExceededException;
import com.repositorio.mvp.common.result.ServiceResult;

import io.github.bucket4j.Bucket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponseException;
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
    private final RateLimitingService rateLimitingService;

    @PostMapping("/login")
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    @ApiResponse(responseCode = "200", description = "Código 2FA enviado com sucesso")
    public ResponseEntity<MessageResponseDTO> login(@Valid @RequestBody @NonNull LoginRequestDTO loginRequest, @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        Bucket bucket = rateLimitingService.resolveLoginBucket(ip);

        if (!bucket.tryConsume(1)) {
            throw new ErrorResponseException(HttpStatus.TOO_MANY_REQUESTS, ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, MessageConstants.Auth.ERR_RATELIMIT_EXCEEDED), null);
        }

        ServiceResult<Void> result = loginService.initiateLogin(loginRequest, ip);

        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.ok(new MessageResponseDTO(MessageConstants.Auth.LOGIN_2FA_SENT));
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PostMapping("/verify-2fa")
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    @ApiResponse(responseCode = "200", description = "Token JWT gerado com sucesso")
    public ResponseEntity<TokenResponseDTO> verify2FA(@Valid @RequestBody @NonNull Verify2FARequestDTO verifyRequest,
            @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        ServiceResult<String> result = loginService.verify2FAAndGenerateToken(verifyRequest, ip);

        return switch (result) {
            case ServiceResult.Success<String> s -> ResponseEntity.ok(new TokenResponseDTO(s.data()));
            case ServiceResult.NotFound<String> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<String> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PostMapping("/logout")
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT fornecido no cabeçalho de autorização, encerrando a sessão ativa.")
    @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") @NonNull String token) {
        SecurityContextHolder.clearContext();
        ServiceResult<Void> result = sessionService.logout(token);
        
        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.noContent().build();
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicita a recuperação de senha", description = "Gera um token de redefinição e envia um link para o e-mail informado.")
    @ApiResponse(responseCode = "200", description = "Token de recuperação enviado com sucesso")
    public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @RequestBody @NonNull ForgotPasswordRequestDTO forgotPasswordRequestDTO,
            @NonNull HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        
        ServiceResult<Void> result = passwordRecoveryService.initiatePasswordRecovery(
            forgotPasswordRequestDTO.email(), 
            ip
        );

        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.ok(new MessageResponseDTO(MessageConstants.Auth.FORGOT_PASSWORD_SENT));
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefine a senha do usuário", description = "Recebe o token de recuperação e a nova senha para atualizar as credenciais.")
    @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso")
    public ResponseEntity<MessageResponseDTO> resetPassword(@Valid @RequestBody @NonNull ResetPasswordRequestDTO request,
            @NonNull HttpServletRequest httpRequest) {
        String ip = ClientIp.getClientIp(httpRequest);

        ServiceResult<Void> result = passwordRecoveryService.resetPassword(
                request.token(),
                request.newPassword());

        return switch (result) {
            case ServiceResult.Success<Void> _ -> {
                log.info(LogMessageConstants.AUTH.PASSWORD_RESET_SUCCESS, ip);
                yield ResponseEntity.ok(new MessageResponseDTO(MessageConstants.Auth.PASSWORD_RESET_SUCCESS));
            }
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }
}
