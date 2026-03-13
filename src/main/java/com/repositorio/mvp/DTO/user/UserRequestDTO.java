package com.repositorio.mvp.DTO.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 8, max = 50, message = "O nome deve ter no mínimo 8 e no máximo 50 caracteres")
    String name,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email não é válido")
    @Size(min = 8, max = 50, message = "O email não pode ter mais de 50 caracteres")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(max = 80, message = "A senha deve ter no mínimo 8 e no máximo 50 caracteres")
    String password
) {
} 