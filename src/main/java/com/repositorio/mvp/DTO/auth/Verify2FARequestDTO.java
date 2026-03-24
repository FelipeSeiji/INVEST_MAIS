package com.repositorio.mvp.DTO.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Verify2FARequestDTO(
    @Schema(description = "Email do usuário",example = "exemplo@gmail.com")
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email não é válido")
    @Size(min = 8, max = 50, message = "O email não pode ter mais de 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "O email deve seguir o formato valido, ex: nome@gmail.com")
    String email,

    @Schema(description = "Senha do usuário", example = "Senha@123")
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 6, message = "O código deve ter 6 caracteres")
    String code
) {}