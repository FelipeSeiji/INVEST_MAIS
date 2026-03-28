package com.repositorio.mvp.mock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;
import com.repositorio.mvp.controller.UserController;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.interfaces.UserQueryService;
import com.repositorio.mvp.shared.UserConstants;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private UserQueryService userQueryService;

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
        when(userCommandService.createUser(requestDTO)).thenReturn(mockResponseDTO);

        UserResponseDTO response = userController.createUser(requestDTO);
        assertNotNull(response);
        assertEquals(mockUserId, response.id());
        assertEquals(requestDTO.name(), response.name());

        verify(userCommandService).createUser(requestDTO);
    }

    @Test
    public void updateUser_PassesDataToService_AndReturnsUpdatedUser() {
        UserUpdateRequestDTO updateRequest = new UserUpdateRequestDTO("User Updated", "newEmail@gmail.com");
        UserResponseDTO updatedResponse = new UserResponseDTO(mockUserId, "User Updated", "newEmail@gmail.com");

        when(userCommandService.updateUserById(eq(mockUserId), any(UserUpdateRequestDTO.class)))
                .thenReturn(updatedResponse);

        UserResponseDTO response = userController.updateUser(mockUserId, updateRequest);

        assertEquals("User Updated", response.name());
        verify(userCommandService).updateUserById(mockUserId, updateRequest);
    }

    @Test
    public void deleteUser_CallsServiceToDelete() {
        userController.deleteUser(mockUserId);

        verify(userCommandService).deleteUserById(mockUserId);
    }

    @Test
    public void findUserByID_ReturnsUserFromService() {
        when(userQueryService.findUserById(mockUserId)).thenReturn(mockResponseDTO);

        UserResponseDTO response = userController.findUserByID(mockUserId);

        assertNotNull(response);
        assertEquals(mockUserId, response.id());
        verify(userQueryService).findUserById(mockUserId);
    }

    @Test
    public void getAllUsers_ReturnsListFromService() {
        List<UserResponseDTO> expectedList = List.of(mockResponseDTO);
        when(userQueryService.listAllUsers()).thenReturn(expectedList);

        List<UserResponseDTO> responseList = userController.getAllUsers();

        assertEquals(1, responseList.size());
        assertEquals(mockResponseDTO, responseList.get(0));
        verify(userQueryService).listAllUsers();
    }
}