package com.repositorio.mvp.domain.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @Schema(description = "Email do usuário",example = "example@gmail.com")
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email não é válido")
    @Size(min = 8, max = 50, message = "O email não pode ter mais de 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "O email deve seguir o formato valido, ex: example@gmail.com")
    String email,

    @Schema(description = "Senha do usuário", example = "Password@123")
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 50, message = "A senha deve ter no mínimo 8 e no máximo 50 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).*$", message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial")
    String password
) {}