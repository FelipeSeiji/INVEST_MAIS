package com.repositorio.mvp.domain.auth.login.DTO;

import com.repositorio.mvp.common.validation.auth.Valid2FACode;
import com.repositorio.mvp.common.validation.user.ValidEmail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição de verificação de 2FA")
public record Verify2FARequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Código de verificação", example = "123456")
    @Valid2FACode
    String code
) {}