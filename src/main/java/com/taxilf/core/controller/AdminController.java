package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.entity.User;
import com.taxilf.core.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/users")
    public List<User> getUsers() {
        return adminService.getUsers();
    }

}
