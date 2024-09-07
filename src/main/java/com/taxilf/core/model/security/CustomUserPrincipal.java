package com.taxilf.core.model.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CustomUserPrincipal {    
    private Long id;
    private Long userId;
    private String role;
}