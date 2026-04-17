package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Objeto de requisição contendo os dados necessários para criar ou atualizar um ativo")
public record AssetRequestDTO(
    @Schema(description = "Identificador único do ativo (ignorado na criação)", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @NotBlank(message = "O ticker do ativo é obrigatório")
    @Schema(description = "O código/ticker do ativo no mercado", example = "BPAC11")
    String ticker,
    
    @PositiveOrZero(message = "O valor atual não pode ser negativo")
    @Schema(description = "O valor total investido ou patrimonial projetado atual deste ativo na carteira", example = "15000.50")
    BigDecimal currentPositionValue,
    
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    @Schema(description = "Quantidade de cotas ou frações mantidas (se aplicável)", example = "100.5")
    BigDecimal quantity,
    
    @PositiveOrZero(message = "O preço médio não pode ser negativo")
    @Schema(description = "Preço médio pago por unidade deste ativo", example = "149.50")
    BigDecimal averagePrice,
    
    @Schema(description = "Pontuação em valor bruto computada das avaliações (se houver)", example = "3")
    Integer rawScore
) {}