package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de categoria")
public record CategoryResponseDTO(
    @Schema(description = "ID único da categoria", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Nome da categoria", example = "Ações")
    String name,
    
    @Schema(description = "Percentual alvo desta categoria", example = "25.0")
    BigDecimal targetPercentage,
    
    @Schema(description = "Lista de ativos")
    List<AssetResponseDTO> assets
) {}
