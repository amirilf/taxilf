package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.model.projection.PassengerProfileProjection;
import com.taxilf.core.service.PassengerService;

import org.springframework.web.bind.annotation.GetMapping;


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
    

}
