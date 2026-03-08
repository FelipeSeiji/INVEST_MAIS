package com.repositorio.mvp.service;

import static com.repositorio.mvp.shared.UserConstants.USER;

import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.DTO.UserResponseDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void createUser_WithValidData_ReturnsUserResponseDTO() {
        User user = User.builder()
            .name(USER.getName())
            .email(USER.getEmail())
            .password(USER.getPassword())
            .build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO createdUser = userService.createUser(USER);
        assertThat(createdUser).isNotNull();
        
        assertThat(createdUser.getName()).isEqualTo(USER.getName());
        assertThat(createdUser.getEmail()).isEqualTo(USER.getEmail());
    }

    @Test
    public void createUser_WithInvalidData_ReturnsNull() {
        when(userRepository.save(any(User.class))).thenReturn(null);

        UserResponseDTO createdUser = userService.createUser(USER);
        assertThat(createdUser).isNull();
    }
}