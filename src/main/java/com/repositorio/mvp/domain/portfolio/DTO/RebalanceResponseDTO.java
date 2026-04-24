package com.repositorio.mvp.domain.portfolio.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A resposta mestra produzida pela Engine de Rebalanceamento. Informa visualmente os aportes em cascata (Carteira -> Categoria -> Ativo).")
public record RebalanceResponseDTO(
    @Schema(description = "Valor financeiro total pós-simulação (valor atual + aporte executado)", example = "105000.00")
    BigDecimal totalValue,
    
    @Schema(description = "Agrupamentos com as recomendações destrinchadas em nível categórico")
    List<CategoryRebalanceDTO> categories
) {
    @Schema(description = "Agrupador indicativo da saúde e do distanciamento alvo vs atual de uma categoria inteira")
    public record CategoryRebalanceDTO(
        @Schema(description = "Identificador único da Categoria", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "Nome da categoria em análise", example = "Renda Fixa")
        String name,
        
        @Schema(description = "Peso percentual que ela representa atualmente no portfólio", example = "30.0")
        BigDecimal currentPercentage,
        
        @Schema(description = "A meta/peso em que ela deve chegar perante o plano estratégico", example = "35.0")
        BigDecimal targetPercentage,
        
        @Schema(description = "Detalhamento e diretrizes de ação para cada ativo individual dentro desta categoria")
        List<AssetRebalanceDTO> assets
    ) {}

    @Schema(description = "A diretriz base e final para um ativo específico. Diz exatamente se deve comprar e quanto.")
    public record AssetRebalanceDTO(
        @Schema(description = "UUID interno do ativo avaliado", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        
        @Schema(description = "Ticket ou nome de mercado (Ex: PETR4)", example = "PETR4")
        String ticker,
        
        @Schema(description = "Seu peso individual vigente dentro do patrimônio atual", example = "2.1")
        BigDecimal currentPercentage,
        
        @Schema(description = "O peso alvo diluído ideal que este único ativo deveria ter perante todas as camadas", example = "3.5")
        BigDecimal targetPercentage,
        
        @Schema(description = "A recomendação exata em dinheiro (R$) que deveria ser comprada agora para curar o gap. Pode ser 0.0 se não for hora de comprar.", example = "1400.00")
        BigDecimal suggestedAporte,
        
        @Schema(description = "Instrução declarativa e visual para o Front-End pintar e exibir ao utilizador. Ex: 'MANTER', 'COMPRAR'", example = "COMPRAR")
        String action
    ) {}
}
