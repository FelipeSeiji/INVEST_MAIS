package com.repositorio.mvp.domain.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.repositorio.mvp.common.validation.ValidEmail;

import com.repositorio.mvp.common.validation.ValidPassword;

public record LoginRequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @ValidPassword
    String password
) {}