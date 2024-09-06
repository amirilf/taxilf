package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.taxilf.core.model.dto.request.TripPointDTO;
import com.taxilf.core.model.dto.request.TripRequestDTO;
import com.taxilf.core.service.TripService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/fare")
    public Double getMethodName(@RequestBody TripPointDTO tripPointDTO) {
        return tripService.getFare(tripPointDTO);
    }

    @GetMapping("/passenger/request")
    public ResponseEntity<String> passengerRequest(@Valid @RequestBody TripRequestDTO tripRequestDTO) {
        return tripService.passengerRequest(tripRequestDTO);
    }

    @GetMapping("/passenger/status")
    public String passengerStatus(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/passenger/cancel")
    public String passengerCancel(@RequestParam String param) {
        return new String();
    }

}
