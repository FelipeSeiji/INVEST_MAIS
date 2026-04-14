package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetRequest(
    UUID id,
    String ticker,
    BigDecimal currentPositionValue,
    Integer rawScore
) {}