package com.repositorio.mvp.domain.user.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.repositorio.mvp.common.validation.ValidEmail;
import com.repositorio.mvp.common.validation.ValidName;

public record UserUpdateRequestDTO(
        @Schema(description = "Nome do usuário", example = "User Name") 
        @ValidName 
        String name,

        @Schema(description = "Email do usuário", example = "example@gmail.com") 
        @ValidEmail 
        String email,
        
        @Schema(description = "Senha atual do usuário", example = "Password@123") 
        @NotBlank(message = "A confirmação da senha atual é obrigatória") 
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_\\-]).*$", message = "A senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial") 
        String currentPassword
    ) {
}