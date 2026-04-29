package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.List;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

/**
 * Interface de consulta para categorias de ativos.
 * Define as operações de leitura para listar as categorias configuradas na carteira.
 */
public interface AssetCategoryQueryService {
    /**
     * Lista todas as categorias de ativos vinculadas ao usuário autenticado.
     * 
     * @return Resultado contendo a lista de DTOs das categorias do usuário.
     */
    ServiceResult<List<CategoryResponseDTO>> listUserCategories();
}
