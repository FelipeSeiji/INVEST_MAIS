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
import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Asset Commands", description = "Operações de escrita para ativos da carteira")
public class AssetCommandController {

    private final AssetCommandService assetCommandService;

    @PostMapping("/categories/{categoryId}/assets")
    @Operation(summary = "Adiciona um novo ativo em uma categoria")
    @ApiResponse(responseCode = "201", description = "Ativo criado com sucesso")
    public ResponseEntity<AssetResponseDTO> createAsset(@PathVariable UUID categoryId, @Valid @RequestBody AssetRequestDTO request) {
        ServiceResult<AssetResponseDTO> result = assetCommandService.createAsset(categoryId, request);
        
        return switch (result) {
            case ServiceResult.Success<AssetResponseDTO> s -> ResponseEntity.status(HttpStatus.CREATED).body(s.data());
            case ServiceResult.NotFound<AssetResponseDTO> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<AssetResponseDTO> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados de um ativo (Ticker/Valor)")
    @ApiResponse(responseCode = "200", description = "Ativo atualizado com sucesso")
    public ResponseEntity<AssetResponseDTO> updateAsset(@PathVariable UUID id, @Valid @RequestBody AssetRequestDTO request) {
        ServiceResult<AssetResponseDTO> result = assetCommandService.updateAsset(id, request);
        
        return switch (result) {
            case ServiceResult.Success<AssetResponseDTO> s -> ResponseEntity.ok(s.data());
            case ServiceResult.NotFound<AssetResponseDTO> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<AssetResponseDTO> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um ativo")
    @ApiResponse(responseCode = "204", description = "Ativo deletado com sucesso")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {
        ServiceResult<Void> result = assetCommandService.deleteAsset(id);
        
        return switch (result) {
            case ServiceResult.Success<Void> _ -> ResponseEntity.noContent().build();
            case ServiceResult.NotFound<Void> n -> throw new ErrorResponseException(HttpStatus.NOT_FOUND, ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, n.message()), null);
            case ServiceResult.Error<Void> e -> throw new ErrorResponseException(HttpStatus.BAD_REQUEST, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message()), null);
        };
    }
}
