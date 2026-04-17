package com.repositorio.mvp.domain.user.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import com.repositorio.mvp.common.validation.user.ValidEmail;
import com.repositorio.mvp.common.validation.user.ValidName;
import com.repositorio.mvp.common.validation.user.ValidPassword;

public record UserRequestDTO(
    @Schema(description = "Nome do usuário", example = "User Name")
    @ValidName
    String name,

    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @ValidPassword
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password
) {
}