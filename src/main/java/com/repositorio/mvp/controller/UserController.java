package com.repositorio.mvp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.interfaces.UserQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    
    // POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo usuario", description = "Cria um novo usuario e insere no banco de dados")
    @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) { 
        return userCommandService.createUser(userRequestDTO);
    }
    
    // GET /api/users
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Lista todos os usuarios", description = "Lista todos os usuarios do banco de dados")
    public List<UserResponseDTO> getAllUsers() {
        return userQueryService.listAllUsers();
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleta um usuario", description = "Deleta um usuario do banco de dados pelo id")
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso")
    public void deleteUser(@PathVariable UUID id) {
        userCommandService.deleteUserById(id);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca um usuario pelo id", description = "Busca um usuario do banco de dados pelo id")
    public UserResponseDTO findUserByID(@PathVariable UUID id) {
        return userQueryService.findUserById(id);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario do banco de dados pelo id")
    public UserResponseDTO updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return userCommandService.updateUserById(id, userUpdateRequestDTO);
    }
}