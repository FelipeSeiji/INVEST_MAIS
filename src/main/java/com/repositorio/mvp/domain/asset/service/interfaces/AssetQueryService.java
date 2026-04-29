package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;

import lombok.NonNull;

/**
 * Interface de consulta para ativos individuais.
 * Fornece métodos para recuperar informações de ativos filtrados por categoria.
 */
public interface AssetQueryService {
    /**
     * Lista todos os ativos pertencentes a uma categoria específica.
     * 
     * @param categoryId UUID da categoria para filtro.
     * @return Resultado contendo a lista de DTOs dos ativos encontrados.
     */
    ServiceResult<List<AssetResponseDTO>> listAssetsByCategory(@NonNull UUID categoryId);
}
