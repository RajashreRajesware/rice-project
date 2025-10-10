package com.example.model_project.service.Impl;

import com.example.model_project.dto.RegisterDto;
import com.example.model_project.entity.AppUser;
import com.example.model_project.repository.UserRepo;
import com.example.model_project.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUser register(RegisterDto dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        String role = dto.getRole() == null || dto.getRole().isBlank() ? "ROLE_USER" : ("ROLE_" + dto.getRole().toUpperCase());
        user.setRole(role);
        return userRepo.save(user);
    }

    @Override
    public AppUser findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElse(null);
    }
}
