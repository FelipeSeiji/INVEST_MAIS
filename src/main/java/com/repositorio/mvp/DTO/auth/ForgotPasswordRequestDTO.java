package com.repositorio.mvp.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
    @Email(message = "O email não é válido")
    @NotBlank(message = "O email é obrigatório")
    String email
) {}
