package com.repositorio.mvp.domain.asset.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Queries", description = "Operações de leitura para ativos da carteira")
public class AssetQueryController {

    private final AssetQueryService assetQueryService;

    @GetMapping("/categories/{categoryId}/assets")
    @Operation(summary = "Lista ativos de uma categoria específica")
    @ApiResponse(responseCode = "200", description = "Lista de ativos retornada com sucesso")
    public List<AssetResponseDTO> listAssetsByCategory(@PathVariable UUID categoryId) {
        return assetQueryService.listAssetsByCategory(categoryId);
    }
}
