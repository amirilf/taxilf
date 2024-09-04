package com.taxilf.core.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taxilf.core.service.TripService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/fare")
    public Double getMethodName(
        @RequestParam(required = false) Double slat,
        @RequestParam(required = false) Double slon,
        @RequestParam(required = false) Double elat,
        @RequestParam(required = false) Double elon,
        @RequestParam(required = false) String sname,
        @RequestParam(required = false) String ename
    ) {
        return tripService.getFare(slat, slon, elat, elon, sname, ename);
    }
    
}
