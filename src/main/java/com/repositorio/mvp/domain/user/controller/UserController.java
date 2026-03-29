package com.repositorio.mvp.domain.user.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável pelo gerenciamento (CRUD) de usuários.
 * Implementa controles de acesso baseados em roles e ID do proprietário para evitar IDOR (Insecure Direct Object Reference).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    
    /**
     * Endpoint público para registro de um novo usuário na plataforma.
     * @param userRequestDTO DTO contendo os dados do novo usuário.
     * @return DTO com os dados salvos do usuário recém-criado.
     */
    // POST /api/users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo usuario", description = "Cria um novo usuario e insere no banco de dados")
    @ApiResponse(responseCode = "201", description = "Usuario criado com sucesso")
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) { 
        return userCommandService.createUser(userRequestDTO);
    }
    
    /**
     * Endpoint para listar todos os usuários da base.
     * @return Lista contendo os dados públicos de todos os usuários.
     */
    // GET /api/users
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os usuarios", description = "Lista todos os usuarios do banco de dados")
    public List<UserResponseDTO> getAllUsers() {
        return userQueryService.listAllUsers();
    }

    /**
     * Remove um usuário do sistema pelo seu identificador.
     * Regra de Segurança: Apenas um Administrador pode excluir contas através desta rota.
     * @param id UUID do usuário que será deletado.
     */
    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deleta um usuario", description = "Deleta um usuario do banco de dados pelo id")
    @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso")
    public void deleteUser(@PathVariable UUID id) {
        userCommandService.deleteUserById(id);
    }

    /**
     * Busca os detalhes de um usuário específico.
     * Regra de Segurança (Anti-IDOR): O usuário logado só pode buscar os próprios dados, a menos que seja um ADMIN.
     * @param id UUID do usuário alvo da busca.
     * @return DTO com as informações do usuário encontrado.
     */
    // GET /api/users/{id}
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @Operation(summary = "Busca um usuario pelo id", description = "Busca um usuario do banco de dados pelo id")
    public UserResponseDTO findUserByID(@PathVariable UUID id) {
        return userQueryService.findUserById(id);
    }

    /**
     * Atualiza os dados cadastrais de um usuário (ex: Nome, Email).
     * Regra de Segurança (Anti-IDOR): O usuário logado só pode alterar o próprio perfil, a menos que seja um ADMIN.
     * @param id UUID do usuário que será alterado.
     * @param userUpdateRequestDTO DTO contendo os novos dados do perfil.
     * @return DTO refletindo o estado atualizado do usuário.
     */
    // PUT /api/users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario do banco de dados pelo id")
    public UserResponseDTO updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return userCommandService.updateUserById(id, userUpdateRequestDTO);
    }
}