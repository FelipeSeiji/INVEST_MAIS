package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import com.repositorio.mvp.common.validation.asset.ValidFinanceAmount;
import com.repositorio.mvp.common.validation.asset.ValidTicker;

@Schema(description = "Objeto de requisição contendo os dados necessários para criar ou atualizar um ativo")
public record AssetRequestDTO(
    @Schema(description = "Identificador único do ativo (ignorado na criação)", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @ValidTicker
    @Schema(description = "O código/ticker do ativo no mercado", example = "BPAC11")
    String ticker,
    
    @ValidFinanceAmount
    @Schema(description = "O valor total investido ou patrimonial projetado atual deste ativo na carteira", example = "15000.50")
    BigDecimal currentPositionValue,
    
    @ValidFinanceAmount
    @Schema(description = "Quantidade de cotas ou frações mantidas (se aplicável)", example = "100.5")
    BigDecimal quantity,
    
    @ValidFinanceAmount
    @Schema(description = "Preço médio pago por unidade deste ativo", example = "149.50")
    BigDecimal averagePrice,
    
    @Schema(description = "Pontuação em valor bruto computada das avaliações (se houver)", example = "3")
    Integer rawScore
) {}