package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetResponse(
    UUID id,
    String ticker,
    BigDecimal currentPositionValue,
    BigDecimal quantity,
    BigDecimal averagePrice,
    Integer score
) {}
