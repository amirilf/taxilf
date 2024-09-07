package com.taxilf.core.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taxilf.core.model.enums.Gender;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
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
    private Gender gender = Gender.NONE;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.NONE;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    @JsonIgnore
    private Point location;
    
}
