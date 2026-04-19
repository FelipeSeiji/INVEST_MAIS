package com.repositorio.mvp.domain.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ResultMapper;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;

import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.repositorio.mvp.infrastructure.exception.RateLimitExceededException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Commands", description = "Operações de escrita para cadastro e gerenciamento de perfis de usuário")
public class UserCommandController {
    private final UserCommandService userCommandService;
    private final RateLimitingService rateLimitingService;

    @PostMapping
    @Operation(summary = "Cria um novo usuário e inicializa sua carteira")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody @NonNull UserRequestDTO userRequestDTO,
            HttpServletRequest request) {
        
        String ip = ClientIp.getClientIp(request);
        Bucket bucket = rateLimitingService.resolveRegistrationBucket(ip);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException(MessageConstants.Auth.ERR_RATELIMIT_EXCEEDED);
        }

        return ResultMapper.created(userCommandService.createUser(userRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exclui permanentemente um usuário pelo ID")
    @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso")
    public ResponseEntity<Void> deleteUser(@PathVariable @NonNull UUID id) {
        return ResultMapper.noContent(userCommandService.deleteUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @Operation(summary = "Atualiza o perfil de um usuário (Nome/E-mail)")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable @NonNull UUID id,
            @Valid @RequestBody @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        return ResultMapper.ok(userCommandService.updateUserById(id, userUpdateRequestDTO));
    }
}
