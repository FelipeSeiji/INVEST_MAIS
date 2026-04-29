package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;

import lombok.NonNull;

/**
 * Interface de comando para gerenciamento de ativos individuais.
 * Define as operações de escrita para criar, atualizar e excluir ativos vinculados a categorias.
 */
public interface AssetCommandService {
    /**
     * Cria um novo ativo dentro de uma categoria específica.
     * 
     * @param categoryId UUID da categoria pai.
     * @param request DTO com os dados do ativo (ticker, quantidade, preços).
     * @return Resultado contendo o DTO do ativo criado.
     */
    ServiceResult<AssetResponseDTO> createAsset(@NonNull UUID categoryId, @NonNull AssetRequestDTO request);

    /**
     * Atualiza os dados de um ativo existente.
     * 
     * @param id UUID do ativo a ser atualizado.
     * @param request DTO com os novos dados.
     * @return Resultado contendo o DTO do ativo atualizado.
     */
    ServiceResult<AssetResponseDTO> updateAsset(@NonNull UUID id, @NonNull AssetRequestDTO request);

    /**
     * Remove um ativo da carteira.
     * 
     * @param id UUID do ativo a ser excluído.
     * @return Resultado indicando sucesso ou erro.
     */
    ServiceResult<Void> deleteAsset(@NonNull UUID id);
}
