package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.taxilf.core.model.dto.request.TripPointDTO;
import com.taxilf.core.model.dto.request.TripRequestDTO;
import com.taxilf.core.model.dto.response.DriverSearchDTO;
import com.taxilf.core.model.dto.response.PassengerStatusDTO;
import com.taxilf.core.service.TripService;

import jakarta.validation.Valid;

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

    // PASSENGER
    @PostMapping("/passenger/request")
    public ResponseEntity<String> passengerRequest(@Valid @RequestBody TripRequestDTO tripRequestDTO) {
        return tripService.passengerRequest(tripRequestDTO);
    }

    @GetMapping("/passenger/status")
    public PassengerStatusDTO passengerStatus() {
        return tripService.passengerStatus();
    }

    @PostMapping("/passenger/cancel")
    public ResponseEntity<String> passengerCancel() {
        return tripService.passengerCancel();
    }

    // DRIVER
    @PostMapping("/driver/request")
    public DriverSearchDTO driverRequest() {
        return tripService.driverRequest();
    }

    @PostMapping("/driver/search")
    public DriverSearchDTO driverSearch() {
        return tripService.driverSearch();
    }

    @PostMapping("/driver/pick/{id}")
    public ResponseEntity<String> driverPick(@RequestParam Long id) {
        return tripService.driverPick(id);
    }

}
