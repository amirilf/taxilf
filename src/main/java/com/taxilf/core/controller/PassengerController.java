package com.taxilf.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taxilf.core.model.projection.PassengerProfileProjection;
import com.taxilf.core.model.projection.PersonalLocationProjection;
import com.taxilf.core.model.projection.PointProjection;
import com.taxilf.core.service.PassengerService;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    private final PassengerService passengerService;

    PassengerController(PassengerService passengerService){
        this.passengerService = passengerService;
    }

    @GetMapping("/profile")
    public PassengerProfileProjection getProfile() {
        return passengerService.getProfile();
    }
    
    // personal-locations
    @GetMapping("/pls")
    public List<PersonalLocationProjection> getPersonalLocations() {
        return passengerService.getPersonalLocations();
    }

    // get PointDTO of a pl
    @GetMapping("/pls/{name}")
    public PointProjection getPersonalLocation(@PathVariable String name) {
        return passengerService.getPersonalLocation(name);
    }

}
