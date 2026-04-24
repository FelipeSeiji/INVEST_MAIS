package com.repositorio.mvp.domain.asset.service;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.repository.AssetRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetQueryService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do serviço de consultas para ativos (Assets).
 * Fornece métodos para recuperar informações sobre os ativos vinculados às categorias da carteira.
 */
@Service
@RequiredArgsConstructor
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    /**
     * Lista todos os ativos pertencentes a uma categoria específica do usuário.
     * 
     * @param categoryId UUID da categoria cujos ativos devem ser recuperados.
     * @return Lista de DTOs representando os ativos encontrados na categoria.
     * @throws EntityNotFoundException Caso a categoria informada não pertença à carteira do usuário.
     */
    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<AssetResponseDTO>> listAssetsByCategory(@NonNull UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId())
            .map(category -> {
                List<AssetResponseDTO> assets = assetRepository.findAllByCategoryId(categoryId).stream()
                    .map(assetMapper::toResponse)
                    .toList();
                return ServiceResult.success(assets);
            })
            .orElse(ServiceResult.notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }
}
