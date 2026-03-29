package com.repositorio.mvp.domain.user.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDTO(
        @Schema(description = "Nome do usuário", example = "User Name") @NotBlank(message = "O nome é obrigatório") @Size(max = 50, message = "O nome deve ter no mínimo 8 e no máximo 50 caracteres") @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "O nome deve conter apenas letras") String name,

        @Schema(description = "Email do usuário", example = "example@gmail.com") @NotBlank(message = "O email é obrigatório") @Email(message = "O email não é válido") @Size(min = 8, max = 50, message = "O email não pode ter mais de 50 caracteres") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).*$", message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial") String email) {
}