package com.repositorio.mvp.domain.question.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.result.ResultMapper;
import com.repositorio.mvp.domain.question.DTO.EvaluationRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;
import com.repositorio.mvp.domain.question.service.interfaces.QuestionCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question Commands", description = "Operações de escrita para questões e critérios de avaliação")
public class QuestionCommandController {

    private final QuestionCommandService commandService;

    @PostMapping("/categories/{categoryId}")
    @Operation(summary = "Cria uma nova questão para uma categoria")
    @ApiResponse(responseCode = "201", description = "Questão criada com sucesso")
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @PathVariable UUID categoryId, 
            @Valid @RequestBody QuestionRequestDTO request) {
        return ResultMapper.created(commandService.createQuestion(categoryId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o texto de uma questão")
    @ApiResponse(responseCode = "200", description = "Questão atualizada com sucesso")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable UUID id, 
            @Valid @RequestBody QuestionRequestDTO request) {
        return ResultMapper.ok(commandService.updateQuestion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma questão")
    @ApiResponse(responseCode = "204", description = "Questão removida com sucesso")
    public ResponseEntity<Void> deleteQuestion(@PathVariable UUID id) {
        return ResultMapper.noContent(commandService.deleteQuestion(id));
    }

    @PostMapping("/assets/{assetId}/evaluations")
    @Operation(summary = "Salva as avaliações de um ativo baseadas nas questões")
    @ApiResponse(responseCode = "204", description = "Avaliações salvas com sucesso")
    public ResponseEntity<Void> saveEvaluations(
            @PathVariable UUID assetId, 
            @Valid @RequestBody List<EvaluationRequestDTO> evaluations) {
        return ResultMapper.noContent(commandService.saveEvaluations(assetId, evaluations));
    }
}
