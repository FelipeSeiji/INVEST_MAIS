package com.repositorio.mvp.domain.asset.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets/categories")
@RequiredArgsConstructor
@Tag(name = "Asset Category Queries", description = "Operações de leitura para categorias de ativos")
public class AssetCategoryQueryController {
    private final AssetCategoryQueryService queryService;

    @GetMapping
    @Operation(summary = "Lista todas as categorias do usuário")
    @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    public List<CategoryResponseDTO> listCategories() {
        return queryService.listUserCategories();
    }
}
