package com.repositorio.mvp.domain.asset.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryQueryService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetCategoryQueryServiceImpl implements AssetCategoryQueryService {

    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listUserCategories() {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findAllByPortfolioId(portfolio.getId()).stream()
                .map(assetMapper::toResponse)
                .toList();
    }
}
