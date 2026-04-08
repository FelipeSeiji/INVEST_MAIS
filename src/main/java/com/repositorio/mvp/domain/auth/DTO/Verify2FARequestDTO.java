package com.repositorio.mvp.domain.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.repositorio.mvp.common.validation.ValidEmail;

public record Verify2FARequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @ValidEmail
    String email,

    @Schema(description = "Código de verificação", example = "123456")
    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
    String code
) {}