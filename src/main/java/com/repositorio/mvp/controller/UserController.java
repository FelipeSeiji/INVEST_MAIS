package com.repositorio.mvp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.interfaces.UserQueryService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    
    //POST /api/users
    @PostMapping
    @Operation(summary = "Cria um novo usuario", description = "Cria um novo usuario e insere no banco de dados")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO){ 
        return new ResponseEntity<>(userCommandService.createUser(userRequestDTO), HttpStatus.CREATED);
    }
    
    //GET /api/users
    @GetMapping
    @Operation(summary = "Lista todos os usuarios", description = "Lista todos os usuarios do banco de dados")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userQueryService.listAllUsers());
    }

    //DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um usuario", description = "Deleta um usuario do banco de dados pelo id")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userCommandService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    //GET /api/users/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuario pelo id", description = "Busca um usuario do banco de dados pelo id")
    public ResponseEntity<UserResponseDTO> findUserByID(@PathVariable UUID id) {
        return ResponseEntity.ok(userQueryService.findUserById(id));
    }

    //PUT /api/users/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario do banco de dados pelo id")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return ResponseEntity.ok(userCommandService.updateUserById(id, userUpdateRequestDTO));
    }
    
}
