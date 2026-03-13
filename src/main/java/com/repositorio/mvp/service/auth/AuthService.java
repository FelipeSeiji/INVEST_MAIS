package com.repositorio.mvp.service.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    // Injeção de dependência limpa e imutável exigida pelo Lombok
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco (que agora retorna um Optional<User>)
        return userRepository.findByEmail(email)
                // Se encontrar, converte a entidade User para UserDetailsImpl
                .map(UserDetailsImpl::new) 
                // Se não encontrar, lança a exceção padrão do Spring Security
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }
}