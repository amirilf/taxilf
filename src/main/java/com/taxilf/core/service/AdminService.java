package com.taxilf.core.service;

import org.springframework.stereotype.Service;

import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class AdminService {
    
    private final UserRepository userRepository;

    AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
