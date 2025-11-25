package com.example.model_project.controller;

import com.example.model_project.dto.RegisterDto;
import com.example.model_project.repository.UserRepo;
import com.example.model_project.service.UserService;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class AuthController {

    private final UserService userService;
    private final UserRepo userRepo;

    public AuthController(UserService userService, UserRepo userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/", "/register"})
    public String showRegister(Model model) {
        model.addAttribute("user", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") RegisterDto dto, Model model) {

        if (userRepo.findByUsername(dto.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }

        userService.register(dto);
        return "redirect:/login?success";
    }

}
