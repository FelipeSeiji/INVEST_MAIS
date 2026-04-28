package com.repositorio.mvp.domain.auth.DTO;

import com.repositorio.mvp.common.validation.auth.ValidToken;
import com.repositorio.mvp.common.validation.user.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição de redefinição de senha")
public record ResetPasswordRequestDTO (
    @Schema(description = "Token de recuperação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @ValidToken
    String token,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @ValidPassword
    String newPassword
){}