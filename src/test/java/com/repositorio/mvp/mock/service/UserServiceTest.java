package com.repositorio.mvp.mock.service;

import static com.repositorio.mvp.shared.UserConstants.INVALID_USER;
import static com.repositorio.mvp.shared.UserConstants.USER;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.mapper.UserMapper;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.UserService;
import com.repositorio.mvp.service.validation.UserValidation;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userServiceImpl;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserValidation userValidation;

    UUID userId = UUID.randomUUID();

    @Test
    public void createUser_WithValidData_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password("senha_encriptada")
            .build();

        UserResponseDTO expectedResponse =
            new UserResponseDTO(userId, USER.name(), USER.email());

        when(userMapper.toUser(any(UserRequestDTO.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encriptada");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(expectedResponse);

        UserResponseDTO createdUser = userServiceImpl.createUser(USER);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(userId);
        assertThat(createdUser.name()).isEqualTo(USER.name());
        assertThat(createdUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void createUser_WithEmailAlreadyInUse_ThrowException() {

        doThrow(new IllegalArgumentException("Email já está em uso"))
            .when(userValidation)
            .validadeNewEmail(anyString());

        assertThatThrownBy(() -> {
            userServiceImpl.createUser(INVALID_USER);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Email já está em uso");
    }

    @Test
    public void findUserById_WithValidId_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password(USER.password())
            .build();

        UserResponseDTO expectedResponse =
            new UserResponseDTO(userId, USER.name(), USER.email());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO foundUser = userServiceImpl.findUserById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.id()).isEqualTo(userId);
        assertThat(foundUser.name()).isEqualTo(USER.name());
        assertThat(foundUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void findUserById_WithInvalidId_ThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            userServiceImpl.findUserById(userId);
        }).isInstanceOf(EntityNotFoundException.class)
          .hasMessage("Usuário não encontrado");
    }

    @Test
    public void listAllUsers_ReturnsListOfUserResponseDTO() {
        UUID userId2 = UUID.randomUUID();

        User user1 = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password(USER.password())
            .build();

        User user2 = User.builder()
            .id(userId2)
            .name("Maria")
            .email("maria@gmail.com")
            .password("1234")
            .build();

        UserResponseDTO expectedResponse1 =
            new UserResponseDTO(userId, USER.name(), USER.email());

        UserResponseDTO expectedResponse2 =
            new UserResponseDTO(userId2, "Maria", "maria@gmail.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toUserResponseDTO(user1)).thenReturn(expectedResponse1);
        when(userMapper.toUserResponseDTO(user2)).thenReturn(expectedResponse2);

        List<UserResponseDTO> users = userServiceImpl.listAllUsers();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).id()).isEqualTo(userId);
        assertThat(users.get(0).name()).isEqualTo(USER.name());
        assertThat(users.get(1).id()).isEqualTo(userId2);
        assertThat(users.get(1).name()).isEqualTo("Maria");
    }
}