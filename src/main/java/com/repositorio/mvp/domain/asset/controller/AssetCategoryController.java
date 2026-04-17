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

import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.service.AssetCategoryCommandService;
import com.repositorio.mvp.domain.asset.service.AssetCategoryQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets/categories")
@RequiredArgsConstructor
@Tag(name = "Asset Categories", description = "Endpoints segregados para gerenciamento de categorias da carteira")
public class AssetCategoryController {
    private final AssetCategoryCommandService commandService;
    private final AssetCategoryQueryService queryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova categoria", description = "Cria uma categoria de ativos vinculada à carteira do usuário")
    public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return commandService.createCategory(request);
    }

    @GetMapping
    @Operation(summary = "Lista todas as categorias do usuário")
    public List<CategoryResponseDTO> listCategories() {
        return queryService.listUserCategories();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public CategoryResponseDTO updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO request) {
        return commandService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma categoria")
    public void deleteCategory(@PathVariable UUID id) {
        commandService.deleteCategory(id);
    }
}
