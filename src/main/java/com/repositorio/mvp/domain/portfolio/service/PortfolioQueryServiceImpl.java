package com.repositorio.mvp.domain.portfolio.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.asset.engine.AssetScoreCalculator;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;
import com.repositorio.mvp.domain.portfolio.engine.RebalanceEngine;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.service.interfaces.PortfolioQueryService;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioQueryServiceImpl implements PortfolioQueryService {

    private final UserContextService userContextService;
    private final AssetScoreCalculator assetScoreCalculator;
    private final RebalanceEngine rebalanceEngine;

    @Override
    @Transactional(readOnly = true)
    public RebalanceResponseDTO calculateRebalance(@NonNull BigDecimal aporteAmount) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolioWithCategoriesAndAssets();

        return rebalanceEngine.calculate(portfolio, aporteAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getPortfolioSummary() {
        Portfolio portfolio = userContextService.getCurrentUserPortfolioWithCategoriesAndAssets();

        BigDecimal totalValue = calculateTotalValue(portfolio);
        
        List<AssetCategory> activeCategories = portfolio.getCategories().stream()
                .filter(c -> c.getAssets().stream().anyMatch(a -> assetScoreCalculator.calculateScore(a) > 0))
                .toList();

        BigDecimal sumActiveOriginalTargets = activeCategories.stream()
                .map(AssetCategory::getTargetPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalAssets = (int) portfolio.getCategories().stream()
                .flatMap(c -> c.getAssets().stream())
                .count();

        List<DashboardResponseDTO.CategorySummaryDTO> summaries = new ArrayList<>();

        for (AssetCategory category : portfolio.getCategories()) {
            BigDecimal currentCategoryValue = category.getAssets().stream()
                    .map(Asset::getCurrentPositionValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentPercentage = totalValue.compareTo(BigDecimal.ZERO) > 0
                    ? currentCategoryValue.multiply(new BigDecimal("100")).divide(totalValue, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            BigDecimal redistributedTarget = BigDecimal.ZERO;
            if (activeCategories.contains(category) && sumActiveOriginalTargets.compareTo(BigDecimal.ZERO) > 0) {
                redistributedTarget = category.getTargetPercentage()
                        .multiply(new BigDecimal("100"))
                        .divide(sumActiveOriginalTargets, 2, RoundingMode.HALF_UP);
            }

            summaries.add(new DashboardResponseDTO.CategorySummaryDTO(
                    category.getId(),
                    category.getName(),
                    currentPercentage,
                    redistributedTarget,
                    currentCategoryValue,
                    category.getAssets().size()
            ));
        }

        return new DashboardResponseDTO(totalValue, summaries, totalAssets);
    }

    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        return portfolio.getCategories().stream()
                .flatMap(c -> c.getAssets().stream())
                .map(Asset::getCurrentPositionValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
