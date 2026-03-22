package com.repositorio.mvp.DTO.asset;

import java.math.BigDecimal;
import java.util.UUID;

import com.repositorio.mvp.model.enums.AssetCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AssetRequestDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    Integer amount,

    @NotNull(message = "O valor atual é obrigatório")
    @PositiveOrZero(message = "O valor atual não pode ser negativo")
    BigDecimal currentValue,

    @NotNull(message = "A nota é obrigatória")
    @PositiveOrZero(message = "A nota não pode ser negativa")
    Double note,

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço deve ser maior que zero")
    BigDecimal price,

    @NotNull(message = "A recomendação é obrigatória")
    @PositiveOrZero(message = "A recomendação não pode ser negativa")
    Double recommend,

    @NotNull(message = "A porcentagem é obrigatória")
    @PositiveOrZero(message = "A porcentagem não pode ser negativa")
    Double percentage,

    @NotNull(message = "O ID do usuário é obrigatório")
    UUID userId,

    @NotNull(message = "A categoria é obrigatória")
    AssetCategory categoryActive
) {}
