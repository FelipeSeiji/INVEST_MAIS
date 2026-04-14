package com.repositorio.mvp.domain.asset.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.domain.asset.model.AssetEvaluation;

@Repository
public interface AssetEvaluationRepository extends JpaRepository<AssetEvaluation, UUID> {
    List<AssetEvaluation> findAllByAssetId(UUID assetId);
}