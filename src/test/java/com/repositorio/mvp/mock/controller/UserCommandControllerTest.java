package com.repositorio.mvp.mock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.controller.UserCommandController;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import com.repositorio.mvp.shared.UserConstants;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class UserCommandControllerTest {

    @InjectMocks
    private UserCommandController userController;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private RateLimitingService rateLimitingService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Bucket bucket;

    private UUID mockUserId;
    private UserResponseDTO mockResponseDTO;

    @BeforeEach
    void setUp() {
        mockUserId = UUID.randomUUID();
        mockResponseDTO = new UserResponseDTO(mockUserId, "User", "example@gmail.com");
    }

    @Test
    public void createUser_PassesDataToService_AndReturnsResponseDTO() {
        UserRequestDTO requestDTO = UserConstants.USER;
        when(rateLimitingService.resolveRegistrationBucket(any())).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);
        when(userCommandService.createUser(requestDTO)).thenReturn(ServiceResult.success(mockResponseDTO));

        ResponseEntity<UserResponseDTO> response = userController.createUser(requestDTO, httpServletRequest);
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals(mockUserId, response.getBody().id());

        verify(userCommandService).createUser(requestDTO);
    }

    @Test
    public void updateUser_PassesDataToService_AndReturnsUpdatedUser() {
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO("User Updated", "newEmail@gmail.com", "Password@123");
        UserResponseDTO updatedResponse = new UserResponseDTO(mockUserId, "User Updated", "newEmail@gmail.com");

        when(userCommandService.updateUserById(eq(mockUserId), any(UserUpdateRequestDTO.class)))
                .thenReturn(ServiceResult.success(updatedResponse));

        ResponseEntity<UserResponseDTO> response = userController.updateUser(mockUserId, updateRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User Updated", response.getBody().name());
        verify(userCommandService).updateUserById(mockUserId, updateRequest);
    }

    @Test
    public void deleteUser_CallsServiceToDelete() {
        when(userCommandService.deleteUserById(mockUserId)).thenReturn(ServiceResult.success(null));
        
        ResponseEntity<Void> response = userController.deleteUser(mockUserId);

        assertEquals(204, response.getStatusCode().value());
        verify(userCommandService).deleteUserById(mockUserId);
    }
}
