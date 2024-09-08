package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.dto.response.TripPointsStatusDTO;
import com.taxilf.core.model.entity.Trip;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.service.AdminService;
import com.taxilf.core.utility.GeometryUtils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/trips")
    public List<Trip> getTrips() {
        return adminService.getTrips();
    }

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/trips/{id}")
    public TripPointsStatusDTO getUITripPoints(@PathVariable Long id) {
        return adminService.getUITripPoints(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/test")
    public TripPointsStatusDTO test() {
        return TripPointsStatusDTO.builder()
                .driverLocation(GeometryUtils.randomPointInMashhad())
                .passengerLocation(GeometryUtils.randomPointInMashhad())
                .start(GeometryUtils.randomPointInMashhad())
                .end(GeometryUtils.randomPointInMashhad())
                .build();
    }
    
}
