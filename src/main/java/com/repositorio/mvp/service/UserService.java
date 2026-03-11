package com.repositorio.mvp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.mapper.UserMapper;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/* Classe de serviço para todos os processos e regras de negocio da entidade USER
 */

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    //Metodo para criar um novo usuário
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        User user = userMapper.toUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    //Metodo para buscar um usuário por ID
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    //Metodo para listar todos os usuários
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toUserResponseDTO)
            .toList();
    }

    //Metodo para excluir um usuário por ID
    @Transactional
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    //Metodo para atualizar um usuário por ID
    @Transactional
    public UserResponseDTO updateByIdUser(UUID id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // Regra de negócio: Validação de e-mail duplicado
        if (userRepository.existsByEmail(userRequestDTO.email()) && !user.getEmail().equals(userRequestDTO.email())) {
            throw new IllegalArgumentException("Email já está em uso por outro usuário.");
        }

        user.setName(userRequestDTO.name());
        user.setEmail(userRequestDTO.email());
        
        if (userRequestDTO.password() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        }

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }
}