package com.repositorio.mvp.domain.portfolio.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Objeto de resposta consolidado que contém todos os dados cruciais para a exibição principal da UI (Dashboard) da carteira do usuário.")
public record DashboardResponseDTO(
    @Schema(description = "Valor total financeiro (R$) posicionado atualmente em todos os ativos da carteira.", example = "150000.00")
    BigDecimal totalValue,
    
    @Schema(description = "Detalhamento e resumo por cada categoria macro de investimento inserida na carteira.")
    List<CategorySummaryDTO> categories,
    
    @Schema(description = "Somatório da quantidade de papéis/ativos únicos registrados dentro da carteira.", example = "14")
    int totalAssets
) {
    @Schema(description = "DTO aninhado que sumariza as finanças e a completude do alvo estratégico de uma categoria isolada.")
    public record CategorySummaryDTO(
        @Schema(description = "ID único da categoria", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "Nome semântico da Categoria", example = "Fundos Imobiliários")
        String name,
        
        @Schema(description = "Percentual atual com base na valoração financeira presente de todos os ativos nela", example = "22.5")
        BigDecimal currentPercentage,
        
        @Schema(description = "Percentual alvo estipulado pelo investidor ou resultante da redistribuição automática de auto-cura", example = "25.0")
        BigDecimal targetPercentage,
        
        @Schema(description = "Somatório financeiro consolidado dos ativos pertencentes a esta categoria", example = "33750.00")
        BigDecimal totalValue,
        
        @Schema(description = "Total de ativos diferentes englobados nessa categoria", example = "5")
        int assetsCount
    ) {}
}
