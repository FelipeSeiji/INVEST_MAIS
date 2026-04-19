package com.repositorio.mvp.mock.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.controller.UserQueryController;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;

@ExtendWith(MockitoExtension.class)
public class UserQueryControllerTest {

    @InjectMocks
    private UserQueryController userController;

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
    public void findUserByID_ReturnsUserFromService() {
        when(userQueryService.findUserById(mockUserId)).thenReturn(mockResponseDTO);

        UserResponseDTO response = userController.findUserByID(mockUserId);

        assertNotNull(response);
        assertEquals(mockUserId, response.id());
        verify(userQueryService).findUserById(mockUserId);
    }

    @Test
    public void getAllUsers_ReturnsPageFromService() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<UserResponseDTO> expectedPage = new PageImpl<>(List.of(mockResponseDTO));
        when(userQueryService.listAllUsers(pageable)).thenReturn(expectedPage);

        Page<UserResponseDTO> responsePage = userController.getAllUsers(pageable);

        assertEquals(1, responsePage.getTotalElements());
        assertEquals(mockResponseDTO, responsePage.getContent().get(0));
        verify(userQueryService).listAllUsers(pageable);
    }
}
