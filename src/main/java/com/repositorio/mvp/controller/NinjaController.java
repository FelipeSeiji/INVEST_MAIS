package com.repositorio.mvp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.model.User;
import com.repositorio.mvp.service.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/users")
public class NinjaController {
    @Autowired
    private UserService userService;
    
    //localhost:8080/api/users/add
    //METODO POST
    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    
    // METODO GET BY ID
    //localhost:8080/api/users/all
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.listAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserByID(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findUserById(id);
        
        if(optionalUser.isPresent()){
            return ResponseEntity.ok(optionalUser.get());
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Desculpe ninja não encontrado");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateNinja(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
}
