package com.example.model_project.service;

import com.example.model_project.dto.RegisterDto;
import com.example.model_project.entity.AppUser;

public interface UserService {


    AppUser register(RegisterDto dto);

    AppUser findByUsername(String username);
}
