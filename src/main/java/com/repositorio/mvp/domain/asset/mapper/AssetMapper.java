package com.repositorio.mvp.domain.asset.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {

    @Mapping(target = "evaluations", ignore = true)
    @Mapping(target = "category", ignore = true)
    Asset toEntity(AssetRequestDTO request);

    AssetResponseDTO toResponse(Asset asset);

    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "assets", ignore = true)
    AssetCategory toEntity(CategoryRequestDTO request);

    CategoryResponseDTO toResponse(AssetCategory category);
}
