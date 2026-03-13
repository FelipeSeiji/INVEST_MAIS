package com.repositorio.mvp.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email não é válido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    String password
) {}