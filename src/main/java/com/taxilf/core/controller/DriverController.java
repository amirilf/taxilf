package com.taxilf.core.controller;

import com.taxilf.core.model.projection.DriverProfileProjection;
import com.taxilf.core.service.DriverService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/profile")
    public DriverProfileProjection getProfile() {
        return driverService.getProfileById();
    }
}
