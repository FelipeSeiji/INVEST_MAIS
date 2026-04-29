package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

import lombok.NonNull;

/**
 * Interface de comando para gerenciamento de categorias de ativos.
 * Define as operações de escrita para criar, atualizar e excluir categorias na carteira do usuário.
 */
public interface AssetCategoryCommandService {
    /**
     * Cria uma nova categoria de ativos na carteira do usuário atual.
     * 
     * @param request DTO com os dados da nova categoria (nome e percentual alvo).
     * @return Resultado contendo o DTO da categoria criada ou mensagem de erro.
     */
    ServiceResult<CategoryResponseDTO> createCategory(@NonNull CategoryRequestDTO request);

    /**
     * Atualiza os dados de uma categoria de ativos existente.
     * 
     * @param id UUID da categoria a ser atualizada.
     * @param request DTO com os novos dados da categoria.
     * @return Resultado contendo o DTO da categoria atualizada ou mensagem de erro.
     */
    ServiceResult<CategoryResponseDTO> updateCategory(@NonNull UUID id, @NonNull CategoryRequestDTO request);

    /**
     * Remove uma categoria de ativos da carteira do usuário.
     * 
     * @param id UUID da categoria a ser excluída.
     * @return Resultado indicando sucesso ou erro (ex: categoria não encontrada).
     */
    ServiceResult<Void> deleteCategory(@NonNull UUID id);
}
