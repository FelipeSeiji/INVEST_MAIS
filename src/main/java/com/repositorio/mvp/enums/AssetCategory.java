package com.repositorio.mvp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssetCategory {
    ACAO_NACIONAL("AN"),
    FUNDOS_IMOBILIARIOS_NACIONAL("FIN"),
    ACAO_INTERNACIONAL("AI"),
    FUNDO_IMOBILIARIO_INTERNACIONAL("FII"),
    RENDA_FIXA_NACIONAL("RFN"),
    RENDA_FIXA_INTERNACIONAL("RFI"),
    CRIPTOMOEDA("CM");

    private final String codigo;

}