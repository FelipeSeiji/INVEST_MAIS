package com.repositorio.mvp.domain.asset.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets/categories")
@RequiredArgsConstructor
@Tag(name = "Asset Category Commands", description = "Operações de escrita para categorias de ativos")
public class AssetCategoryCommandController {
    private final AssetCategoryCommandService commandService;

    @PostMapping
    @Operation(summary = "Cria uma nova categoria", description = "Cria uma categoria de ativos vinculada à carteira do usuário")
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        ServiceResult<CategoryResponseDTO> result = commandService.createCategory(request);
        
        return switch (result) {
            case ServiceResult.Success<CategoryResponseDTO> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.data());
            case ServiceResult.NotFound<CategoryResponseDTO> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<CategoryResponseDTO> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO request) {
        ServiceResult<CategoryResponseDTO> result = commandService.updateCategory(id, request);
        
        return switch (result) {
            case ServiceResult.Success<CategoryResponseDTO> s -> ResponseEntity.ok(s.data());
            case ServiceResult.NotFound<CategoryResponseDTO> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<CategoryResponseDTO> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma categoria")
    @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        ServiceResult<Void> result = commandService.deleteCategory(id);
        
        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.noContent().build();
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }
}
