package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.dto.LoginDTO;
import com.taxilf.core.model.dto.LoginRequestDTO;
import com.taxilf.core.model.dto.RegisterDTO;
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
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerPassengerDTO){
        return authService.register(registerPassengerDTO); 
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOTP(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.requestOTP(loginRequestDTO.getPhone());
    }

}
