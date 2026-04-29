package com.repositorio.mvp.domain.user.service.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;

/**
 * Interface de consulta para gestão de usuários.
 * Define as operações de leitura e busca de informações de usuários e detalhes de segurança.
 */
public interface UserQueryService {
    /**
     * Busca um usuário pelo seu identificador único.
     * 
     * @param id UUID do usuário.
     * @return Resultado contendo o DTO de resposta do usuário.
     */
    ServiceResult<UserResponseDTO> findUserById(UUID id);

    /**
     * Lista todos os usuários cadastrados de forma paginada.
     * 
     * @param pageable Configuração de paginação.
     * @return Página contendo os DTOs dos usuários.
     */
    ServiceResult<Page<UserResponseDTO>> listAllUsers(Pageable pageable);

    /**
     * Carrega os detalhes de segurança do usuário para o Spring Security.
     * Utilizado principalmente durante a autenticação via Token.
     * 
     * @param subjectId Identificador (geralmente UUID em string) do usuário.
     * @return UserDetails preenchido com as permissões e credenciais.
     */
    UserDetails loadUserDetailsById(String subjectId);
}
