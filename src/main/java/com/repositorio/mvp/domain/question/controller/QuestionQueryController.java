package com.repositorio.mvp.domain.question.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;
import com.repositorio.mvp.domain.question.service.interfaces.QuestionQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Question Queries", description = "Operações de leitura para perguntas qualitativas")
public class QuestionQueryController {
    private final QuestionQueryService queryService;

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Lista perguntas de uma categoria")
    @ApiResponse(responseCode = "200", description = "Lista de perguntas retornada com sucesso")
    public List<QuestionResponseDTO> listByCategoryId(@PathVariable UUID categoryId) {
        return queryService.listByCategoryId(categoryId);
    }
}
