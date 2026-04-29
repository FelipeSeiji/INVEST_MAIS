package com.repositorio.mvp.domain.user.service;

import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementação do serviço de consulta para gestão de usuários.
 * Provê métodos para leitura de dados de perfil e carregamento de detalhes
 * de segurança para o framework de segurança.
 */
@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Busca um usuário específico pelo seu identificador único (UUID).
     * 
     * @param id UUID do usuário a ser localizado.
     * @return DTO contendo os dados públicos do usuário encontrado.
     * @throws EntityNotFoundException Caso o identificador não corresponda a nenhum usuário no sistema.
     */
    @Override
    @Transactional(readOnly = true)
    public ServiceResult<UserResponseDTO> findUserById(@NonNull UUID id) {
        return userRepository.findById(id)
            .map(user -> ServiceResult.success(userMapper.toUserResponseDTO(user)))
            .orElse(ServiceResult.notFound(MessageConstants.User.NOT_FOUND));
    }

    /**
     * Recupera uma lista paginada de todos os usuários cadastrados no sistema.
     * 
     * @param pageable Objeto contendo as informações de paginação (página, tamanho, ordenação).
     * @return Page contendo os DTOs dos usuários dentro dos critérios de paginação.
     */
    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Page<UserResponseDTO>> listAllUsers(@NonNull Pageable pageable) {
        Page<UserResponseDTO> users = userRepository.findAll(pageable)
            .map(userMapper::toUserResponseDTO);
        return ServiceResult.success(users);
    }

    /**
     * Localiza e carrega os detalhes de segurança do usuário para o Spring Security.
     * Método central para a autorização stateless via Token JWT.
     * 
     * @param subjectId ID do usuário (como String) extraído do assunto do Token.
     * @return Objeto UserDetailsImpl compatível com o ecossistema de segurança do Spring.
     * @throws IllegalArgumentException Caso o ID no token não corresponda a um usuário ativo.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserDetailsById(@NonNull String subjectId) {
        User user = userRepository.findById(UUID.fromString(subjectId))
            .orElseThrow(() -> new IllegalArgumentException(
                MessageConstants.User.NOT_FOUND_FOR_TOKEN
            ));
        return new UserDetailsImpl(user);
    }
}
