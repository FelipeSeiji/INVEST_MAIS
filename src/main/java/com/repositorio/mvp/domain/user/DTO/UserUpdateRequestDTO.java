package com.repositorio.mvp.domain.user.DTO;

import com.repositorio.mvp.common.validation.user.ValidEmail;
import com.repositorio.mvp.common.validation.user.ValidName;
import com.repositorio.mvp.common.validation.user.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateRequestDTO(
        @Schema(description = "Nome do usuário", example = "User Name") 
        @ValidName 
        String name,

        @Schema(description = "Email do usuário", example = "example@gmail.com") 
        @ValidEmail 
        String email,
        
        @Schema(description = "Senha atual do usuário", example = "Password@123") 
        @ValidPassword
        String currentPassword
    ) {
}