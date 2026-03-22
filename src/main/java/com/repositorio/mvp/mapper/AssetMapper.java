package com.repositorio.mvp.mapper;

import org.mapstruct.Mapper;

import com.repositorio.mvp.DTO.asset.AssetRequestDTO;
import com.repositorio.mvp.DTO.asset.AssetResponseDTO;
import com.repositorio.mvp.model.Asset;

@Mapper(componentModel = "spring")
public interface AssetMapper {
    AssetResponseDTO toAssetResponseDTO(Asset asset);
    Asset toEntity(AssetRequestDTO assetRequestDTO); 
}