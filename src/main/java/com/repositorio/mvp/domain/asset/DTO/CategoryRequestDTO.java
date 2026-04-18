package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import com.repositorio.mvp.common.validation.asset.ValidCategoryName;
import com.repositorio.mvp.common.validation.asset.ValidTargetPercentage;

@Schema(description = "Objeto de requisição para a criação ou alteração de uma Categoria de Investimentos")
public record CategoryRequestDTO(
    @Schema(description = "ID único da categoria (ignorado na criação)", example = "123e4567-e89b-12d3-a456-426614174000") 
    UUID id,

    @ValidCategoryName
    @Schema(description = "Nome descritivo para agrupar investimentos (ex: Ações Brasileiras, Renda Fixa)", example = "Ações")
    String name,

    @ValidTargetPercentage
    @Schema(description = "Percentual alvo (meta) desta categoria dentro do respectivo portfólio", example = "30.0")
    BigDecimal targetPercentage,

    @Schema(description = "Opcional: Listagem inicial de ativos a serem persistidos junto a esta categoria") 
    List<AssetRequestDTO> assets
) {
    
}
