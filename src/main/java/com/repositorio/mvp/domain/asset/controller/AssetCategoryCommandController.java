package com.repositorio.mvp.domain.asset.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets/categories")
@RequiredArgsConstructor
@Tag(name = "Asset Category Commands", description = "Operações de escrita para categorias de ativos")
public class AssetCategoryCommandController {
    private final AssetCategoryCommandService commandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova categoria", description = "Cria uma categoria de ativos vinculada à carteira do usuário")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return commandService.createCategory(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    public CategoryResponseDTO updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO request) {
        return commandService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma categoria")
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso")
    public void deleteCategory(@PathVariable UUID id) {
        commandService.deleteCategory(id);
    }
}
