package com.repositorio.mvp.domain.user.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
 * Implementa controles de acesso baseados em roles e ID do proprietário para
 * evitar IDOR (Insecure Direct Object Reference).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    /**
     * Endpoint público para o registro de um novo usuário na plataforma.
     * Realiza a criação da conta base e das credenciais de segurança.
     * 
     * @param userRequestDTO DTO contendo os dados mandatórios para criação da
     *                       conta.
     * @return DTO contendo os dados públicos do usuário recém-registrado.
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
     * Recupera uma lista paginada de todos os usuários do sistema.
     * Acesso restrito exclusivamente a perfis com a role 'ADMIN'.
     * 
     * @param pageable Configurações de paginação (padrão: 20 registros).
     * @return Página de usuários em formato DTO.
     */
    // GET /api/users
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista os usuarios com paginação", description = "Retorna uma página de usuários do sistema")
    public Page<UserResponseDTO> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return userQueryService.listAllUsers(pageable);
    }

    /**
     * Exclui permanentemente um usuário da base de dados.
     * Ação irreversível e restrita a Administradores.
     * 
     * @param id UUID do usuário alvo da exclusão.
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
     * Recupera os detalhes públicos de um perfil de usuário.
     * Regra de Acesso: O próprio usuário pode visualizar seus dados, ou um ADMIN.
     * 
     * @param id UUID do usuário consultado.
     * @return DTO com as informações do perfil.
     * @throws EntityNotFoundException Caso o usuário não seja localizado.
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
     * Atualiza os dados de perfil (Nome/Email) de um usuário.
     * Regra de Acesso: Apenas o proprietário da conta ou um ADMIN pode realizar a
     * alteração.
     * Nota: Alterações críticas exigem a confirmação da senha atual no DTO.
     * 
     * @param id                   UUID do usuário cujos dados serão alterados.
     * @param userUpdateRequestDTO Novos dados e senha de confirmação.
     * @return DTO com o perfil atualizado.
     */
    // PUT /api/users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal.user.id.toString()")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Atualiza um usuario", description = "Atualiza um usuario do banco de dados pelo id")
    public UserResponseDTO updateUser(@PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return userCommandService.updateUserById(id, userUpdateRequestDTO);
    }
}