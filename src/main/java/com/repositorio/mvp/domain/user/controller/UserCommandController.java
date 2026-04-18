package com.repositorio.mvp.domain.user.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Commands", description = "Operações de escrita para cadastro e gerenciamento de perfis de usuário")
public class UserCommandController {
    private final UserCommandService userCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo usuario", description = "Cria um novo usuario e insere no banco de dados")
    @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso")
    public UserResponseDTO createUser(@Valid @RequestBody @NonNull UserRequestDTO userRequestDTO) {
        return userCommandService.createUser(userRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta um usuario", description = "Deleta um usuario do banco de dados pelo id")
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso")
    public void deleteUser(@PathVariable @NonNull UUID id) {
        userCommandService.deleteUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario do banco de dados pelo id")
    public UserResponseDTO updateUser(@PathVariable @NonNull UUID id,
            @Valid @RequestBody @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        return userCommandService.updateUserById(id, userUpdateRequestDTO);
    }
}
