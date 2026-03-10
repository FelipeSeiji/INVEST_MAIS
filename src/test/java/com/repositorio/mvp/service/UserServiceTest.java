package com.repositorio.mvp.service;

import static com.repositorio.mvp.shared.UserConstants.INVALID_USER;
import static com.repositorio.mvp.shared.UserConstants.USER;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    UUID userId = UUID.randomUUID();

    @Test
    public void createUser_WithValidData_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password(USER.password())
            .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO createdUser = userService.createUser(USER);
        
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(userId);
        assertThat(createdUser.name()).isEqualTo(USER.name());
        assertThat(createdUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void createUser_WithInvalidData_ThrowException() {
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Dados inválidos"));
        
        assertThatThrownBy(() -> {
            userService.createUser(INVALID_USER);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void findUserById_WithValidId_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password(USER.password())
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO foundUser = userService.findUserById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.id()).isEqualTo(userId);
        assertThat(foundUser.name()).isEqualTo(USER.name());
        assertThat(foundUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void findUserById_WithInvalidId_ThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> {
            userService.findUserById(userId);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void listAllUsers_ReturnsListOfUserResponseDTO() {
        User user1 = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .password(USER.password())
            .build();

        User user2 = User.builder()
            .id(userId)
            .name("Maria")
            .email("maria@gmail.com")
            .password("1234")
            .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        
        List<UserResponseDTO> users = userService.listAllUsers();
        
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).id()).isEqualTo(userId);
        assertThat(users.get(0).name()).isEqualTo(USER.name());
        assertThat(users.get(0).email()).isEqualTo(USER.email());
        assertThat(users.get(1).id()).isEqualTo(userId);
        assertThat(users.get(1).name()).isEqualTo("Maria");
        assertThat(users.get(1).email()).isEqualTo("maria@gmail.com");
    }
}