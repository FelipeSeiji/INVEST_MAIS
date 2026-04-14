package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;

public record RebalanceRequest(
    BigDecimal aporteAmount,
    BigDecimal totalCurrentPortfolio,
    List<CategoryRequest> categories
) {}