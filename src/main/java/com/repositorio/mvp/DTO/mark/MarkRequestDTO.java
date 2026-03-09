package com.repositorio.mvp.DTO.mark;

import com.repositorio.mvp.enums.CategoryActive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MarkRequestDTO(
    @NotNull(message = "A porcentagem é obrigatória")
    @PositiveOrZero(message = "A porcentagem não pode ser negativa")
    Double percentage,

    @NotBlank(message = "A label é obrigatória")
    @Size(max = 50, message = "A label deve ter no máximo 50 caracteres")
    String label,

    @NotNull(message = "A categoria é obrigatória")
    CategoryActive categoryActive
) {}