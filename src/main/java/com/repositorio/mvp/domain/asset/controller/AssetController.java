package com.repositorio.mvp.domain.asset.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.asset.DTO.AssetRequest;
import com.repositorio.mvp.domain.asset.DTO.AssetResponse;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequest;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponse;
import com.repositorio.mvp.domain.asset.service.AssetCategoryService;
import com.repositorio.mvp.domain.asset.service.AssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets & Categories", description = "Endpoints para gerenciamento de categorias e ativos da carteira")
public class AssetController {

    private final AssetCategoryService categoryService;
    private final AssetService assetService;

    // --- Categorias ---

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova categoria", description = "Cria uma categoria de ativos vinculada à carteira do usuário")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @GetMapping("/categories")
    @Operation(summary = "Lista todas as categorias do usuário")
    public List<CategoryResponse> listCategories() {
        return categoryService.listUserCategories();
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public CategoryResponse updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma categoria")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }

    // --- Ativos ---

    @PostMapping("/categories/{categoryId}/assets")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona um novo ativo em uma categoria")
    public AssetResponse createAsset(@PathVariable UUID categoryId, @Valid @RequestBody AssetRequest request) {
        return assetService.createAsset(categoryId, request);
    }

    @GetMapping("/categories/{categoryId}/assets")
    @Operation(summary = "Lista ativos de uma categoria específica")
    public List<AssetResponse> listAssetsByCategory(@PathVariable UUID categoryId) {
        return assetService.listAssetsByCategory(categoryId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um ativo (Ticker/Valor)")
    public AssetResponse updateAsset(@PathVariable UUID id, @Valid @RequestBody AssetRequest request) {
        return assetService.updateAsset(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um ativo")
    public void deleteAsset(@PathVariable UUID id) {
        assetService.deleteAsset(id);
    }
}
