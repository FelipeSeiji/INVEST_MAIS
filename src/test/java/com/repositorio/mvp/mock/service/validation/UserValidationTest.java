package com.repositorio.mvp.mock.service.validation;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.UserRegisterValidatorImpl;
import com.repositorio.mvp.domain.user.validation.UserUpdateValidatorImpl;
import com.repositorio.mvp.common.constants.MessageConstants;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    @InjectMocks
    private UserRegisterValidatorImpl registerValidator;

    @InjectMocks
    private UserUpdateValidatorImpl updateValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    private final String TEST_EMAIL = "example@gmail.com";
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        lenient().when(cryptoService.generateSha256Hash(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return DigestUtils.sha256Hex(email.toLowerCase());
        });
    }

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

        assertEquals(MessageConstants.User.EMAIL_ALREADY_IN_USE, exception.getMessage());
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

        assertEquals(MessageConstants.User.EMAIL_ALREADY_IN_USE_BY_OTHER, exception.getMessage());
    }
}