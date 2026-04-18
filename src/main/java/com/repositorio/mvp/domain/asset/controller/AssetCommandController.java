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

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Commands", description = "Operações de escrita para ativos da carteira")
public class AssetCommandController {

    private final AssetCommandService assetCommandService;

    @PostMapping("/categories/{categoryId}/assets")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona um novo ativo em uma categoria")
    @ApiResponse(responseCode = "201", description = "Ativo criado com sucesso")
    public AssetResponseDTO createAsset(@PathVariable UUID categoryId, @Valid @RequestBody AssetRequestDTO request) {
        return assetCommandService.createAsset(categoryId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um ativo (Ticker/Valor)")
    @ApiResponse(responseCode = "200", description = "Ativo atualizado com sucesso")
    public AssetResponseDTO updateAsset(@PathVariable UUID id, @Valid @RequestBody AssetRequestDTO request) {
        return assetCommandService.updateAsset(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um ativo")
    @ApiResponse(responseCode = "204", description = "Ativo deletado com sucesso")
    public void deleteAsset(@PathVariable UUID id) {
        assetCommandService.deleteAsset(id);
    }
}
