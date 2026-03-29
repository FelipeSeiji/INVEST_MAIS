package com.repositorio.mvp.mock.service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.UserValidation;
import org.apache.commons.codec.digest.DigestUtils;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    @InjectMocks
    private UserValidation userValidation;

    @Mock
    private UserRepository userRepository;

    private final String TEST_EMAIL = "example@gmail.com";

    @Test
    public void validadeNewEmail_WhenEmailDoesNotExist_DoesNotThrowException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsByEmailHash(hash)).thenReturn(false);

        assertDoesNotThrow(() -> userValidation.validadeNewEmail(TEST_EMAIL));
        
        verify(userRepository).existsByEmailHash(hash);
    }

    @Test
    public void validadeNewEmail_WhenEmailExists_ThrowsException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsByEmailHash(hash)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userValidation.validadeNewEmail(TEST_EMAIL);
        });

        assertEquals("Email já está em uso", exception.getMessage());
    }

    @Test
    public void validadeUpdateEmail_WhenEmailDoesNotExist_DoesNotThrowException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsByEmailHash(hash)).thenReturn(false);

        assertDoesNotThrow(() -> userValidation.validadeUpdateEmail(TEST_EMAIL, "old_email@gmail.com"));
    }

    @Test
    public void validadeUpdateEmail_WhenEmailExistsButIsTheSame_DoesNotThrowException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsByEmailHash(hash)).thenReturn(true);

        assertDoesNotThrow(() -> userValidation.validadeUpdateEmail(TEST_EMAIL, TEST_EMAIL));
    }

    @Test
    public void validadeUpdateEmail_WhenEmailExistsAndIsDifferent_ThrowsException() {
        String hash = DigestUtils.sha256Hex(TEST_EMAIL.toLowerCase());
        when(userRepository.existsByEmailHash(hash)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userValidation.validadeUpdateEmail(TEST_EMAIL, "old_email@gmail.com");
        });

        assertEquals("Email já está em uso por outro usuário.", exception.getMessage());
    }
}