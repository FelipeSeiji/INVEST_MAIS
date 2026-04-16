package com.repositorio.mvp.domain.question.controller;

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

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.DTO.MessageResponseDTO;
import com.repositorio.mvp.domain.question.DTO.EvaluationRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionResponse;
import com.repositorio.mvp.domain.question.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Questions & Scoring", description = "Endpoints para gerenciamento de perguntas qualitativas e pontuação de ativos")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova pergunta para uma categoria")
    public QuestionResponse createQuestion(@PathVariable UUID categoryId, @Valid @RequestBody QuestionRequest request) {
        return questionService.createQuestion(categoryId, request);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Lista perguntas de uma categoria")
    public List<QuestionResponse> listByCategoryId(@PathVariable UUID categoryId) {
        return questionService.listByCategoryId(categoryId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o texto de uma pergunta")
    public QuestionResponse updateQuestion(@PathVariable UUID id, @Valid @RequestBody QuestionRequest request) {
        return questionService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma pergunta")
    public void deleteQuestion(@PathVariable UUID id) {
        questionService.deleteQuestion(id);
    }

    @PostMapping("/assets/{assetId}/evaluate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Salva as respostas (pontuação) de um ativo", description = "Envia uma lista de respostas para as perguntas da categoria do ativo.")
    public MessageResponseDTO saveEvaluations(@PathVariable UUID assetId, @Valid @RequestBody List<EvaluationRequest> evaluations) {
        questionService.saveEvaluations(assetId, evaluations);
        return new MessageResponseDTO(MessageConstants.Question.EVALUATION_SAVED);
    }
}
