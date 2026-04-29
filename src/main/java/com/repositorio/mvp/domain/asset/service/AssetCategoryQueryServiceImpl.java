package com.repositorio.mvp.domain.asset.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryQueryService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import lombok.RequiredArgsConstructor;

/**
 * Implementação do serviço de consulta para categorias de ativos.
 * Fornece acesso otimizado às categorias cadastradas na carteira do usuário.
 */
@Service
@RequiredArgsConstructor
public class AssetCategoryQueryServiceImpl implements AssetCategoryQueryService {

    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    /**
     * {@inheritDoc}
     * Recupera a carteira do usuário a partir do contexto de segurança e retorna suas categorias.
     */
    @Override
    @Transactional(readOnly = true)
    public ServiceResult<List<CategoryResponseDTO>> listUserCategories() {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        List<CategoryResponseDTO> categories = categoryRepository.findAllByPortfolioId(portfolio.getId()).stream()
            .map(assetMapper::toResponse)
            .toList();
        return ServiceResult.success(categories);
    }
}
