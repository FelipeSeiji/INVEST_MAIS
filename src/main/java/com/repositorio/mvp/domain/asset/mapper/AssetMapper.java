package com.repositorio.mvp.domain.asset.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.repositorio.mvp.domain.asset.DTO.AssetRequest;
import com.repositorio.mvp.domain.asset.DTO.AssetResponse;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequest;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponse;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetMapper {

    @Mapping(target = "evaluations", ignore = true)
    @Mapping(target = "category", ignore = true)
    Asset toEntity(AssetRequest request);

    AssetResponse toResponse(Asset asset);

    @Mapping(target = "portfolio", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "assets", ignore = true)
    AssetCategory toEntity(CategoryRequest request);

    CategoryResponse toResponse(AssetCategory category);
}
