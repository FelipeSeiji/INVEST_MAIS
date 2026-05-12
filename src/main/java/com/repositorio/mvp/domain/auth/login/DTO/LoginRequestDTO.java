package com.repositorio.mvp.domain.auth.login.DTO;

import com.repositorio.mvp.common.validation.user.ValidEmail;
import com.repositorio.mvp.common.validation.user.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição de login")
public record LoginRequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @ValidPassword
    String password
) {}