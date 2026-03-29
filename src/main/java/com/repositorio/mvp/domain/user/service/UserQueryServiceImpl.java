package com.repositorio.mvp.domain.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Busca um usuário específico pelo seu identificador único (UUID).
     * * @param id UUID do usuário.
     * @return DTO contendo os dados do usuário.
     * @throws EntityNotFoundException Se o UUID não existir na base de dados.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     * Nota: Idealmente, em produção, este método deve implementar paginação (Pageable).
     * * @return Lista contendo DTOs de todos os usuários.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> listAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toUserResponseDTO);
    }
}
