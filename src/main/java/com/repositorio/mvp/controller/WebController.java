package com.repositorio.mvp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

import com.repositorio.mvp.service.UserService;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    // temporario para testes 
    // remover depois
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", userService.listAllUsers());
        return "index";
    }
}