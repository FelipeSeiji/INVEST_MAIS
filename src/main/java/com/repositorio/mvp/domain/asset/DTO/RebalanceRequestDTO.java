package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import com.repositorio.mvp.common.validation.asset.ValidFinanceAmount;

@Schema(description = "Objeto de requisição para solicitar um cálculo/simulação de rebalanceamento da carteira de investimentos")
public record RebalanceRequestDTO(
        @ValidFinanceAmount
    @Schema(description = "Montante em dinheiro (novo aporte ou resgate se negativo) a ser alocado na carteira", example = "5000.00")
    BigDecimal aporteAmount,

        @ValidFinanceAmount
    @Schema(description = "Valor financeiro total atualizado consolidado do portfólio (se enviado pelo front, do contrário calculado no back)", example = "100000.00")
    BigDecimal totalCurrentPortfolio,

        @Schema(description = "Lista customizada de categorias e metas caso o usuário esteja simulando um cenário divergente da base de dados") List<CategoryRequestDTO> categories) {
}