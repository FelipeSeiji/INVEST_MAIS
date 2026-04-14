package com.repositorio.mvp.domain.asset.DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CategoryRequest(
    UUID id,
    String name,
    BigDecimal targetPercentage,
    List<AssetRequest> assets
) {}
