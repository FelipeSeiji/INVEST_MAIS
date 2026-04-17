package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de resposta contendo os detalhes de uma categoria pertencente à carteira de um usuário")
public record CategoryResponseDTO(
    @Schema(description = "Identificador único da categoria", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    
    @Schema(description = "Nome da categoria", example = "Ações")
    String name,
    
    @Schema(description = "O percentual financeiro alvo definido pelo investidor para esta classe no longo prazo", example = "25.0")
    BigDecimal targetPercentage,
    
    @Schema(description = "Lista dos ativos abrigados por esta classe/categoria")
    List<AssetResponseDTO> assets
) {}
