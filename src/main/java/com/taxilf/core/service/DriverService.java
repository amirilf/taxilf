package com.taxilf.core.service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.projection.DriverProfileProjection;
import com.taxilf.core.model.repository.DriverRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverProfileProjection getProfileById() {
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return driverRepository.findProfileById(id).orElseThrow(() -> new CustomResourceNotFoundException("Driver not found"));
    }
}