package com.taxilf.core.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.taxilf.core.model.enums.Gender;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false, unique = true, updatable = false)
    private String phone;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private Gender gender = null;

    @Default
    @Column(name = "balance")
    private Double balance = 0d;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NONE;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    
}
