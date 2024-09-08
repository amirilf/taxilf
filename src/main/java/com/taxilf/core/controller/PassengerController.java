package com.taxilf.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
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

    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/profile")
    public PassengerProfileProjection getProfile() {
        return passengerService.getProfile();
    }
    
    // personal-locations
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/pls")
    public List<PersonalLocationProjection> getPersonalLocations() {
        return passengerService.getPersonalLocations();
    }

    // get PointDTO of a pl
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping("/pls/{name}")
    public PointProjection getPersonalLocation(@PathVariable String name) {
        return passengerService.getPersonalLocation(name);
    }

}
