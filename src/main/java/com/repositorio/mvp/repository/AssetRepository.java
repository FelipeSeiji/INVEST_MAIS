package com.repositorio.mvp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.model.Asset;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID>{
    
}
