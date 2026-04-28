package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;

import com.repositorio.mvp.common.validation.asset.ValidFinanceAmount;
import com.repositorio.mvp.common.validation.asset.ValidTicker;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição de ativo")
public record AssetRequestDTO(
    @Schema(description = "Identificador único do ativo", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @ValidTicker
    @Schema(description = "Código/ticker do ativo no mercado", example = "BPAC11")
    String ticker,
    
    @ValidFinanceAmount
    @Schema(description = "Valor posicionado atual deste ativo na carteira", example = "15000.50")
    BigDecimal currentPositionValue,
    
    @ValidFinanceAmount
    @Schema(description = "Quantidade de cotas/frações", example = "100.5")
    BigDecimal quantity,
    
    @ValidFinanceAmount
    @Schema(description = "Preço médio pago por unidade deste ativo", example = "149.50")
    BigDecimal averagePrice,
    
    @Schema(description = "Pontuação em valor bruto", example = "3")
    Integer rawScore
) {}