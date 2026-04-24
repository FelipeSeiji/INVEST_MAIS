package com.repositorio.mvp.mock.service.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.repositorio.mvp.domain.auth.service.login.EmailTwoFactorService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.shared.UserConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailTwoFactorServiceTest{

    @InjectMocks
    private EmailTwoFactorService emailTwoFactorService;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private User mockUser;


    @BeforeEach
    void setUp() {
        mockUser = UserConstants.createMockUser();
    }

    @Test
    public void sendTwoFactorCode_SendsEmailWithCorrectContent() {
        String mockCode = "123456";

        emailTwoFactorService.sendTwoFactorCode(mockUser, mockCode);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(mockUser.getEmail(), sentMessage.getTo()[0]);

        assertEquals("Seu código de acesso (2FA) - MVP", sentMessage.getSubject());   
        assertTrue(sentMessage.getText().contains(mockUser.getName()), "O e-mail deve conter o nome do usuário");
        assertTrue(sentMessage.getText().contains(mockCode), "O e-mail deve conter o código 2FA");
    }
}