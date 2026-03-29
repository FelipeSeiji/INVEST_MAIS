package com.repositorio.mvp.domain.user.validation;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Componente dedicado à execução de validações de regras de negócios avançadas para a entidade User.
 * Isola a lógica de consistência de dados dos serviços principais.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidation {
    private final UserRepository userRepository;

    /**
     * Valida se um e-mail está disponível para um novo cadastro.
     * @param email E-mail a ser validado.
     * @throws IllegalArgumentException Se o e-mail já estiver associado a outra conta.
     */
    public void validadeNewEmail(String email){
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }

    /**
     * Valida de forma segura a alteração de e-mail de uma conta existente, 
     * garantindo que o novo e-mail não pertença a um terceiro.
     * @param email O novo e-mail desejado.
     * @param currentEmail O e-mail atual da conta solicitante.
     * @throws IllegalArgumentException Se o e-mail desejado já for propriedade de outro usuário.
     */
    public void validadeUpdateEmail(String email, String currentEmail) {
        if (userRepository.existsByEmail(email) && !currentEmail.equals(email)){
            throw new IllegalArgumentException("Email já está em uso por outro usuário.");
        }
    }
}
