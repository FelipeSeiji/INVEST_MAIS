package com.repositorio.mvp.domain.user.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

import com.repositorio.mvp.common.validation.ValidEmail;
import com.repositorio.mvp.common.validation.ValidName;
import com.repositorio.mvp.common.validation.ValidPassword;

public record RegisterDTO(
    @Schema(description = "Nome do usuário", example = "User Name")
    @ValidName
    String name,

    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @ValidPassword
    String password
) {
}