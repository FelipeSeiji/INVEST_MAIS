package com.repositorio.mvp.domain.question.controller;

import java.util.List;
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

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.DTO.MessageResponseDTO;
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
@Tag(name = "Question Commands", description = "Operações de escrita para perguntas e avaliações")
public class QuestionCommandController {
    private final QuestionCommandService commandService;

    @PostMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova pergunta para uma categoria")
    @ApiResponse(responseCode = "201", description = "Pergunta criada com sucesso")
    public QuestionResponseDTO createQuestion(@PathVariable UUID categoryId, @Valid @RequestBody QuestionRequestDTO request) {
        return commandService.createQuestion(categoryId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o texto de uma pergunta")
    @ApiResponse(responseCode = "200", description = "Pergunta atualizada com sucesso")
    public QuestionResponseDTO updateQuestion(@PathVariable UUID id, @Valid @RequestBody QuestionRequestDTO request) {
        return commandService.updateQuestion(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove uma pergunta")
    @ApiResponse(responseCode = "204", description = "Pergunta removida com sucesso")
    public void deleteQuestion(@PathVariable UUID id) {
        commandService.deleteQuestion(id);
    }

    @PostMapping("/assets/{assetId}/evaluate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Salva as respostas (pontuação) de um ativo", description = "Envia uma lista de respostas para as perguntas da categoria do ativo.")
    @ApiResponse(responseCode = "200", description = "Respostas salvas com sucesso")
    public MessageResponseDTO saveEvaluations(@PathVariable UUID assetId, @Valid @RequestBody List<EvaluationRequestDTO> evaluations) {
        commandService.saveEvaluations(assetId, evaluations);
        return new MessageResponseDTO(MessageConstants.Question.EVALUATION_SAVED);
    }
}
