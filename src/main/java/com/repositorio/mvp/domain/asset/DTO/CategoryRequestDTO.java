package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.common.validation.asset.ValidCategoryName;
import com.repositorio.mvp.common.validation.asset.ValidTargetPercentage;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição de categoria")
public record CategoryRequestDTO(
    @Schema(description = "ID único da categoria", example = "123e4567-e89b-12d3-a456-426614174000") 
    UUID id,

    @ValidCategoryName
    @Schema(description = "Nome da categoria", example = "Ações")
    String name,

    @ValidTargetPercentage
    @Schema(description = "Percentual alvo desta categoria", example = "30.0")
    BigDecimal targetPercentage,

    @Schema(description = "Lista de ativos") 
    List<AssetRequestDTO> assets
) {
    
}
