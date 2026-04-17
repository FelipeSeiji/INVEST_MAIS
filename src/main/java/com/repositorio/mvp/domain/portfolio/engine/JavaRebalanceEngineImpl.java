package com.repositorio.mvp.domain.portfolio.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.asset.engine.AssetScoreCalculator;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JavaRebalanceEngineImpl implements RebalanceEngine {

    private final AssetScoreCalculator assetScoreCalculator;

    @Override
    public RebalanceResponseDTO calculate(Portfolio portfolio, BigDecimal aporteAmount) {
        
        BigDecimal totalCurrentValue = calculateTotalValue(portfolio);
        BigDecimal newTotalValue = totalCurrentValue.add(aporteAmount);

        List<AssetCategory> activeCategories = portfolio.getCategories().stream()
                .filter(c -> c.getAssets().stream().anyMatch(a -> assetScoreCalculator.calculateScore(a) > 0))
                .toList();

        BigDecimal sumActiveOriginalTargets = activeCategories.stream()
                .map(AssetCategory::getTargetPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPositiveGap = BigDecimal.ZERO;
        java.util.Map<UUID, BigDecimal> rawGaps = new java.util.HashMap<>();
        java.util.Map<UUID, BigDecimal> redistributedTargets = new java.util.HashMap<>();

        for (AssetCategory category : portfolio.getCategories()) {
            boolean isActive = activeCategories.contains(category);

            BigDecimal redistributedTargetPercentage = BigDecimal.ZERO;
            if (isActive && sumActiveOriginalTargets.compareTo(BigDecimal.ZERO) > 0) {
                redistributedTargetPercentage = category.getTargetPercentage()
                        .multiply(new BigDecimal("100"))
                        .divide(sumActiveOriginalTargets, 2, RoundingMode.HALF_UP);
            }
            redistributedTargets.put(category.getId(), redistributedTargetPercentage);

            BigDecimal categoryTargetValue = newTotalValue.multiply(redistributedTargetPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            int totalCategoryScore = category.getAssets().stream()
                    .map(assetScoreCalculator::calculateScore)
                    .filter(score -> score > 0)
                    .mapToInt(Integer::intValue)
                    .sum();

            for (Asset asset : category.getAssets()) {
                int score = assetScoreCalculator.calculateScore(asset);

                BigDecimal assetTargetValue = (isActive && score > 0 && totalCategoryScore > 0)
                        ? categoryTargetValue.multiply(new BigDecimal(score)).divide(
                                new BigDecimal(totalCategoryScore), 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                BigDecimal gap = assetTargetValue.subtract(asset.getCurrentPositionValue());
                if (gap.compareTo(BigDecimal.ZERO) < 0) {
                    gap = BigDecimal.ZERO;
                }

                rawGaps.put(asset.getId(), gap);
                totalPositiveGap = totalPositiveGap.add(gap);
            }
        }

        List<RebalanceResponseDTO.CategoryRebalanceDTO> categoryDTOs = new ArrayList<>();

        for (AssetCategory category : portfolio.getCategories()) {
            BigDecimal redistributedTargetPercentage = redistributedTargets.getOrDefault(category.getId(), BigDecimal.ZERO);

            BigDecimal currentCategoryValue = category.getAssets().stream()
                    .map(Asset::getCurrentPositionValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal categoryCurrentPercentage = totalCurrentValue.compareTo(BigDecimal.ZERO) > 0
                    ? currentCategoryValue.multiply(new BigDecimal("100")).divide(totalCurrentValue, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            List<RebalanceResponseDTO.AssetRebalanceDTO> assetDTOs = new ArrayList<>();

            for (Asset asset : category.getAssets()) {
                BigDecimal assetCurrentPercentage = totalCurrentValue.compareTo(BigDecimal.ZERO) > 0
                        ? asset.getCurrentPositionValue().multiply(new BigDecimal("100"))
                                .divide(totalCurrentValue, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                BigDecimal rawGap = rawGaps.getOrDefault(asset.getId(), BigDecimal.ZERO);
                BigDecimal suggestedAporte = BigDecimal.ZERO;

                if (totalPositiveGap.compareTo(BigDecimal.ZERO) > 0 && aporteAmount.compareTo(BigDecimal.ZERO) > 0) {
                    suggestedAporte = rawGap.multiply(aporteAmount).divide(totalPositiveGap, 2, RoundingMode.HALF_UP);
                }

                assetDTOs.add(new RebalanceResponseDTO.AssetRebalanceDTO(
                        asset.getId(),
                        asset.getTicker(),
                        assetCurrentPercentage,
                        BigDecimal.ZERO,
                        suggestedAporte,
                        suggestedAporte.compareTo(BigDecimal.ZERO) > 0 ? "COMPRAR" : "AGUARDAR"));
            }

            categoryDTOs.add(new RebalanceResponseDTO.CategoryRebalanceDTO(
                    category.getId(),
                    category.getName(),
                    categoryCurrentPercentage,
                    redistributedTargetPercentage,
                    assetDTOs));
        }

        return new RebalanceResponseDTO(totalCurrentValue, categoryDTOs);
    }

    private BigDecimal calculateTotalValue(Portfolio portfolio) {
        return portfolio.getCategories().stream()
                .flatMap(c -> c.getAssets().stream())
                .map(Asset::getCurrentPositionValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
