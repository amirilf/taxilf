package com.taxilf.core.model.entity;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.taxilf.core.model.entity.enums.Gender;
import com.taxilf.core.model.entity.enums.Role;
import com.taxilf.core.model.entity.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, unique = true, updatable = false)
    private String phone;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDate joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender = Gender.NONE;

    @Column(name = "balance", nullable = false)
    private Double balance = 0d;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NONE;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    
}
