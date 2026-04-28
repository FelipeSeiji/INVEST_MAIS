package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de ativo")
public record AssetResponseDTO(
    @Schema(description = "ID único do ativo", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Código/ticker do ativo no mercado", example = "BPAC11")
    String ticker,
    
    @Schema(description = "Valor patrimonial atual posicionado neste ativo", example = "15000.50")
    BigDecimal currentPositionValue,
    
    @Schema(description = "Quantidade de cotas possuídas do ativo", example = "100.5")
    BigDecimal quantity,
    
    @Schema(description = "Preço médio de compra deste ativo", example = "149.50")
    BigDecimal averagePrice,
    
    @Schema(description = "Pontuação em valor bruto", example = "3")
    Integer rawScore
) {}
