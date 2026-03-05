package com.repositorio.mvp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //Metodo para criar um novo usuário
    public User createUser(User user) {
        return userRepository.save(user);
    }

    //Metodo para buscar um usuário por ID
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    //Metodo para excluir um usuário por ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    //Metodo para atualizar um usuário por ID
    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = findUserById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setId(id);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
}
