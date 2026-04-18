package com.repositorio.mvp.domain.user.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Queries", description = "Operações de leitura para busca e listagem de perfis de usuário")
public class UserQueryController {
    private final UserQueryService userQueryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista os usuarios com paginação", description = "Retorna uma página de usuários do sistema")
    public Page<UserResponseDTO> getAllUsers(@PageableDefault(size = 20) @NonNull Pageable pageable) {
        return userQueryService.listAllUsers(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @Operation(summary = "Busca um usuario pelo id", description = "Busca um usuario do banco de dados pelo id")
    public UserResponseDTO findUserByID(@PathVariable @NonNull UUID id) {
        return userQueryService.findUserById(id);
    }
}
