package com.repositorio.mvp.mock.service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;

import com.repositorio.mvp.domain.user.validation.UserRegisterValidatorImpl;
import com.repositorio.mvp.domain.user.validation.UserUpdateValidatorImpl;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    @InjectMocks
    private UserRegisterValidatorImpl registerValidator;

    @InjectMocks
    private UserUpdateValidatorImpl updateValidator;

    @Mock
    private UserRepository userRepository;

    private final String TEST_EMAIL = "example@gmail.com";
    private final UUID userId = UUID.randomUUID();

    @Test
    public void validateRegister_WhenEmailDoesNotExist_DoesNotThrowException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsBySecurityEmailHash(hash)).thenReturn(false);

        UserRequestDTO requestDTO = new UserRequestDTO("Teste", TEST_EMAIL, "senha123");

        assertDoesNotThrow(() -> registerValidator.validate(requestDTO));
        
        verify(userRepository).existsBySecurityEmailHash(hash);
    }

    @Test
    public void validateRegister_WhenEmailExists_ThrowsException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsBySecurityEmailHash(hash)).thenReturn(true);

        UserRequestDTO requestDTO = new UserRequestDTO("Teste", TEST_EMAIL, "senha123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerValidator.validate(requestDTO);
        });

        assertEquals("Email já está em uso", exception.getMessage());
    }

    @Test
    public void validateUpdate_WhenEmailDoesNotExist_DoesNotThrowException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsBySecurityEmailHash(hash)).thenReturn(false);

        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("Novo Nome", TEST_EMAIL, "Password@123");
        User existingUser = User.builder().id(userId).email("old_email@gmail.com").build();

        assertDoesNotThrow(() -> updateValidator.validate(requestDTO, existingUser));
    }

    @Test
    public void validateUpdate_WhenEmailExistsButIsTheSame_DoesNotThrowException() {
        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("Novo Nome", TEST_EMAIL, "Password@123");
        User existingUser = User.builder().id(userId).email(TEST_EMAIL).build();

        assertDoesNotThrow(() -> updateValidator.validate(requestDTO, existingUser));
    }

    @Test
    public void validateUpdate_WhenEmailExistsAndIsDifferent_ThrowsException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsBySecurityEmailHash(hash)).thenReturn(true);

        UserUpdateRequestDTO requestDTO = new UserUpdateRequestDTO("Novo Nome", TEST_EMAIL, "Password@123");
        User existingUser = User.builder().id(userId).email("old_email@gmail.com").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            updateValidator.validate(requestDTO, existingUser);
        });

        assertEquals("Email já está em uso por outro usuário.", exception.getMessage());
    }
}