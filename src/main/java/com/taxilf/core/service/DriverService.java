package com.taxilf.core.service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.projection.DriverProfileProjection;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverProfileProjection getProfileById() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return driverRepository.findProfileById(cup.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Driver not found"));
    }
}