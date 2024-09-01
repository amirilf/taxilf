package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.dto.LoginDTO;
import com.taxilf.core.model.dto.LoginRequestDTO;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register/passenger")
    public Passenger register(@Valid @RequestBody Passenger user){
        return authService.register(user); 
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO.getPhone(), loginDTO.getCode());
    }

    @PostMapping("/login/request")
    public ResponseEntity<String> loginRequest(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.loginRequest(loginRequestDTO.getPhone());
    }

}
