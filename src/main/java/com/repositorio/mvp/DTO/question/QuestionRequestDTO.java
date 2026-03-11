package com.repositorio.mvp.DTO.question;

import com.repositorio.mvp.enums.AssetCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record QuestionRequestDTO(
    @NotNull(message = "A questão é obrigatória")
    @Size(max = 100, message = "A questão deve ter no máximo 100 caracteres")
    String question,

    @NotBlank(message = "A critério é obrigatório")
    @Size(max = 100, message = "O critério deve ter no máximo 100 caracteres")
    String criterion,

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    Integer quantity,

    @NotNull(message = "A resposta é obrigatória")
    Boolean response,

    @NotBlank(message = "O ID da questão é obrigatório")
    @Size(max = 50, message = "O ID da questão deve ter no máximo 50 caracteres")
    String idQuestion,

    @NotNull(message = "A categoria é obrigatória")
    AssetCategory assetCategory
) {}