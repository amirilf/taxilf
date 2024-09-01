package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.dto.LoginDTO;
import com.taxilf.core.model.dto.LoginRequestDTO;
import com.taxilf.core.model.dto.RegisterPassengerDTO;
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
    public ResponseEntity<String> register(@Valid @RequestBody RegisterPassengerDTO registerPassengerDTO){
        return authService.register(registerPassengerDTO); 
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO.getPhone(), loginDTO.getCode());
    }

    @PostMapping("/otp-request")
    public ResponseEntity<String> otpRequest(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.otpRequest(loginRequestDTO.getPhone());
    }

}
