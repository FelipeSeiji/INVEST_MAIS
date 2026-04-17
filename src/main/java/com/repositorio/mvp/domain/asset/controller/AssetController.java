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

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.service.AssetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Endpoints segregados (SRP) para gerenciamento de ativos da carteira")
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/categories/{categoryId}/assets")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona um novo ativo em uma categoria")
    public AssetResponseDTO createAsset(@PathVariable UUID categoryId, @Valid @RequestBody AssetRequestDTO request) {
        return assetService.createAsset(categoryId, request);
    }

    @GetMapping("/categories/{categoryId}/assets")
    @Operation(summary = "Lista ativos de uma categoria específica")
    public List<AssetResponseDTO> listAssetsByCategory(@PathVariable UUID categoryId) {
        return assetService.listAssetsByCategory(categoryId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um ativo (Ticker/Valor)")
    public AssetResponseDTO updateAsset(@PathVariable UUID id, @Valid @RequestBody AssetRequestDTO request) {
        return assetService.updateAsset(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um ativo")
    public void deleteAsset(@PathVariable UUID id) {
        assetService.deleteAsset(id);
    }
}
