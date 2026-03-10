package com.repositorio.mvp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/* Classe de serviço para todos os processos e regras de negocio da entidade USER
 */

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private UserResponseDTO toUserResponseDTO(User user){
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    //Metodo para criar um novo usuário
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        String hashedPassword = passwordEncoder.encode(userRequestDTO.password());

        //Usamos o Builder para transformar o DTO de entrada na Entidade
        User user = User.builder()
            .name(userRequestDTO.name())
            .email(userRequestDTO.email())
            .password(hashedPassword)
            .build();
        User savedUser = userRepository.save(user);

        return toUserResponseDTO(savedUser);
    }

    //Metodo para buscar um usuário por ID
    public UserResponseDTO findUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        return toUserResponseDTO(user);
    }

    public List<UserResponseDTO> listAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toUserResponseDTO)
            .toList();
    }

    //Metodo para excluir um usuário por ID
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    //Metodo para atualizar um usuário por ID
    public UserResponseDTO updateByIdUser(UUID id, UserRequestDTO updatedUser) {
        User userToUpdate = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        // Atualiza a entidade existente com os dados novos do DTO
        userToUpdate.setName(updatedUser.name());
        userToUpdate.setEmail(updatedUser.email());
        userToUpdate.setPassword(passwordEncoder.encode(updatedUser.password()));

        User savedUser = userRepository.save(userToUpdate);
        return toUserResponseDTO(savedUser);
}
}
