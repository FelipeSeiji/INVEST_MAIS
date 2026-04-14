package com.repositorio.mvp.domain.asset.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.domain.asset.model.AssetCategory;

@Repository
public interface AssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {
    List<AssetCategory> findAllByPortfolioId(UUID portfolioId);
    
    Optional<AssetCategory> findByIdAndPortfolioId(UUID id, UUID portfolioId);
}