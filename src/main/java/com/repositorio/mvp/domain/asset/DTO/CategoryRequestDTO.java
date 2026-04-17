package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Objeto de requisição para a criação ou alteração de uma Categoria de Investimentos")
public record CategoryRequestDTO(
    @Schema(description = "ID único da categoria (ignorado na criação)", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Schema(description = "Nome descritivo para agrupar investimentos (ex: Ações Brasileiras, Renda Fixa)", example = "Ações")
    String name,
    
    @PositiveOrZero(message = "O percentual alvo deve ser positivo")
    @Schema(description = "Percentual alvo (meta) desta categoria dentro do respectivo portfólio", example = "30.0")
    BigDecimal targetPercentage,
    
    @Schema(description = "Opcional: Listagem inicial de ativos a serem persistidos junto a esta categoria")
    List<AssetRequestDTO> assets
) {}
