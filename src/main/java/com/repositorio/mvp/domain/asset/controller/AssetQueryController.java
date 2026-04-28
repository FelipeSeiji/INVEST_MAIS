package com.repositorio.mvp.domain.asset.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Queries", description = "Operações de leitura para ativos")
public class AssetQueryController {

    private final AssetQueryService assetQueryService;

    @GetMapping("/categories/{categoryId}/assets")
    @Operation(summary = "Lista ativos de uma categoria", description = "Retorna todos os ativos da categoria especificada")
    @ApiResponse(responseCode = "200", description = "Lista de ativos retornada")
    public ResponseEntity<List<AssetResponseDTO>> listAssetsByCategory(@PathVariable UUID categoryId) {
        ServiceResult<List<AssetResponseDTO>> result = assetQueryService.listAssetsByCategory(categoryId);
        
        return switch (result) {
            case ServiceResult.Success<List<AssetResponseDTO>> s -> ResponseEntity.ok(s.data());
            case ServiceResult.NotFound<List<AssetResponseDTO>> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<List<AssetResponseDTO>> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }
}
