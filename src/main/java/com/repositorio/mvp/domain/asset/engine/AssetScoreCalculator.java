package com.repositorio.mvp.domain.asset.engine;

import com.repositorio.mvp.domain.asset.model.Asset;

public interface AssetScoreCalculator {
    int calculateScore(Asset asset);
}
