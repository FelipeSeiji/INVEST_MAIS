package com.repositorio.mvp.domain.portfolio.engine;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.asset.engine.AssetScoreCalculator;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;
import com.repositorio.mvp.domain.portfolio.engine.calculator.AporteAllocator;
import com.repositorio.mvp.domain.portfolio.engine.calculator.AssetTargetCalculator;
import com.repositorio.mvp.domain.portfolio.engine.calculator.CategoryRedistributionCalculator;
import com.repositorio.mvp.domain.portfolio.engine.calculator.RebalanceResponseFactory;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JavaRebalanceEngineImplTest {

    @Mock
    private AssetScoreCalculator assetScoreCalculator;

    private JavaRebalanceEngineImpl engine;

    private Portfolio portfolio;
    private AssetCategory catAcoes;
    private AssetCategory catFii;
    private Asset assetWeg;
    private Asset assetVale;
    private Asset assetKncr;

    @BeforeEach
    void setUp() {
        CategoryRedistributionCalculator categoryCalculator = new CategoryRedistributionCalculator(assetScoreCalculator);
        AssetTargetCalculator assetCalculator = new AssetTargetCalculator(assetScoreCalculator);
        AporteAllocator aporteAllocator = new AporteAllocator();
        RebalanceResponseFactory responseFactory = new RebalanceResponseFactory();
        
        engine = new JavaRebalanceEngineImpl(categoryCalculator, assetCalculator, aporteAllocator, responseFactory);
        portfolio = Portfolio.builder().build();

        catAcoes = AssetCategory.builder()
                .id(UUID.randomUUID())
                .name("Ações")
                .targetPercentage(new BigDecimal("60"))
                .build();

        catFii = AssetCategory.builder()
                .id(UUID.randomUUID())
                .name("FIIs")
                .targetPercentage(new BigDecimal("40"))
                .build();

        assetWeg = Asset.builder()
                .id(UUID.randomUUID())
                .ticker("WEGE3")
                .currentPositionValue(new BigDecimal("2000"))
                .category(catAcoes)
                .build();

        assetVale = Asset.builder()
                .id(UUID.randomUUID())
                .ticker("VALE3")
                .currentPositionValue(new BigDecimal("1000"))
                .category(catAcoes)
                .build();

        assetKncr = Asset.builder()
                .id(UUID.randomUUID())
                .ticker("KNCR11")
                .currentPositionValue(new BigDecimal("2000"))
                .category(catFii)
                .build();

        catAcoes.setAssets(List.of(assetWeg, assetVale));
        catFii.setAssets(List.of(assetKncr));

        portfolio.setCategories(List.of(catAcoes, catFii));
    }

    @Test
    @DisplayName("Deve rebalancear carteira de forma normal quando todos os ativos têm nota positiva")
    void testRebalance_NormalScenario() {
        // Arrange
        when(assetScoreCalculator.calculateScore(assetWeg)).thenReturn(2);
        when(assetScoreCalculator.calculateScore(assetVale)).thenReturn(1);
        when(assetScoreCalculator.calculateScore(assetKncr)).thenReturn(1);

        BigDecimal aporte = new BigDecimal("5000");

        // Act
        RebalanceResponseDTO response = engine.calculate(portfolio, aporte);

        // Assert
        assertEquals(0, new BigDecimal("10000").compareTo(response.totalValue()));
        assertEquals(2, response.categories().size());

        RebalanceResponseDTO.CategoryRebalanceDTO resAcoes = response.categories().stream()
                .filter(c -> c.name().equals("Ações")).findFirst().get();
        assertEquals(new BigDecimal("60.00"), resAcoes.targetPercentage());

        RebalanceResponseDTO.AssetRebalanceDTO resWeg = resAcoes.assets().stream()
                .filter(a -> a.ticker().equals("WEGE3")).findFirst().get();
        assertEquals(new BigDecimal("2000.00"), resWeg.suggestedAporte());
        assertEquals("COMPRAR", resWeg.action());

        RebalanceResponseDTO.AssetRebalanceDTO resVale = resAcoes.assets().stream()
                .filter(a -> a.ticker().equals("VALE3")).findFirst().get();
        assertEquals(new BigDecimal("1000.00"), resVale.suggestedAporte());
        assertEquals("COMPRAR", resVale.action());

        RebalanceResponseDTO.CategoryRebalanceDTO resFii = response.categories().stream()
                .filter(c -> c.name().equals("FIIs")).findFirst().get();
        assertEquals(new BigDecimal("40.00"), resFii.targetPercentage());

        RebalanceResponseDTO.AssetRebalanceDTO resKncr = resFii.assets().stream()
                .filter(a -> a.ticker().equals("KNCR11")).findFirst().get();
        assertEquals(new BigDecimal("2000.00"), resKncr.suggestedAporte());
        assertEquals("COMPRAR", resKncr.action());
    }

    @Test
    @DisplayName("Deve acionar a Auto-Cura quando uma categoria inteira fica com nota <= 0")
    void testRebalance_AutoCuraScenario() {
        // Arrange
        when(assetScoreCalculator.calculateScore(assetWeg)).thenReturn(1);
        when(assetScoreCalculator.calculateScore(assetVale)).thenReturn(1);
        when(assetScoreCalculator.calculateScore(assetKncr)).thenReturn(0);

        BigDecimal aporte = new BigDecimal("5000");

        // Act
        RebalanceResponseDTO response = engine.calculate(portfolio, aporte);

        // Assert
        RebalanceResponseDTO.CategoryRebalanceDTO resAcoes = response.categories().stream()
                .filter(c -> c.name().equals("Ações")).findFirst().get();
        assertEquals(new BigDecimal("100.00"), resAcoes.targetPercentage());

        RebalanceResponseDTO.AssetRebalanceDTO resWeg = resAcoes.assets().stream()
                .filter(a -> a.ticker().equals("WEGE3")).findFirst().get();
        assertEquals(new BigDecimal("1000.00"), resWeg.suggestedAporte());

        RebalanceResponseDTO.AssetRebalanceDTO resVale = resAcoes.assets().stream()
                .filter(a -> a.ticker().equals("VALE3")).findFirst().get();
        assertEquals(new BigDecimal("4000.00"), resVale.suggestedAporte());

        RebalanceResponseDTO.CategoryRebalanceDTO resFii = response.categories().stream()
                .filter(c -> c.name().equals("FIIs")).findFirst().get();
        assertEquals(new BigDecimal("0"), resFii.targetPercentage());

        RebalanceResponseDTO.AssetRebalanceDTO resKncr = resFii.assets().stream()
                .filter(a -> a.ticker().equals("KNCR11")).findFirst().get();
        assertEquals(new BigDecimal("0"), resKncr.suggestedAporte());
        assertEquals("AGUARDAR", resKncr.action());
    }
}
