package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.taxilf.core.model.dto.request.PointDTO;
import com.taxilf.core.model.dto.request.TripPointDTO;
import com.taxilf.core.model.dto.request.TripRequestDTO;
import com.taxilf.core.model.dto.response.DriverSearchDTO;
import com.taxilf.core.model.dto.response.DriverStatusDTO;
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
    public Double getMethodName(@Valid @RequestBody TripPointDTO tripPointDTO) {
        return tripService.getFare(tripPointDTO);
    }

    @PostMapping("/update-location")
    public ResponseEntity<String> driverUpdateLocation(@Valid @RequestBody PointDTO point) {
        return tripService.updateLocation(point);
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

    @PostMapping("/passenger/pay")
    public ResponseEntity<String> passengerPay() {        
        return tripService.passengerPay();
    }
    

    // DRIVER
    @PostMapping("/driver/request")
    public DriverSearchDTO driverRequest() {
        return tripService.driverRequest();
    }

    @PostMapping("/driver/cancel")
    public ResponseEntity<String> driverCancel() {
        return tripService.driverCancel();
    }

    @PostMapping("/driver/search")
    public DriverSearchDTO driverSearch() {
        return tripService.driverSearch();
    }

    @PostMapping("/driver/pick/{id}")
    public ResponseEntity<String> driverPick(@PathVariable Long id) {
        return tripService.driverPick(id);
    }

    @GetMapping("/driver/status")
    public DriverStatusDTO driverStatus() {
        return tripService.driverStatus();
    }

    @PostMapping("/driver/on-board")
    public ResponseEntity<String> driverOnBoard() {
        return tripService.driverOnBoard();
    }

    @PostMapping("/driver/done")
    public ResponseEntity<String> driverDone() {
        return tripService.driverDone();
    }

    @PostMapping("/driver/cash-confirm")
    public void driverCashConfirm() {
        tripService.driverCashConfirm();
    }

}
