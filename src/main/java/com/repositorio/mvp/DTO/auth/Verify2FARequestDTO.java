package com.repositorio.mvp.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Verify2FARequestDTO(
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    String email,

    @NotBlank(message = "O código 2FA é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 dígitos")
    String code
) {}