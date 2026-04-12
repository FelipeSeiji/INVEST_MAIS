package com.repositorio.mvp.domain.auth.DTO;

import com.repositorio.mvp.common.validation.ValidEmail;

import io.swagger.v3.oas.annotations.media.Schema;

public record ForgotPasswordRequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email
) {}
