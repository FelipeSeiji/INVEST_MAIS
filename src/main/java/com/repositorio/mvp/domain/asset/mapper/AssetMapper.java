package com.repositorio.mvp.domain.asset.mapper;

import org.mapstruct.Mapper;

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.model.Asset;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetResponseDTO toAssetResponseDTO(Asset asset);
    Asset toEntity(AssetRequestDTO assetRequestDTO); 
}