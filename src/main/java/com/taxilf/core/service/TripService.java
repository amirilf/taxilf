package com.taxilf.core.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.dto.request.TripPointDTO;
import com.taxilf.core.model.dto.request.TripRequestDTO;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.TripRequest;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.enums.UserStatus;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.TripRequestRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;
import com.taxilf.core.utility.GeometryUtils;

@Service
public class TripService {

    private final PassengerRepository passengerRepository;
    private final PassengerService passengerService;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final TripRequestRepository tripRequestRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(PassengerService passengerService, PassengerRepository passengerRepository, VehicleTypeRepository vehicleTypeRepository, TripRequestRepository tripRequestRepository) {
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.tripRequestRepository = tripRequestRepository;
    }

    public Double getFare(TripPointDTO tripPointDTO) {

        Point startPoint = getPoint(tripPointDTO.getSlon(), tripPointDTO.getSlat(), tripPointDTO.getSname());
        Point endPoint = getPoint(tripPointDTO.getElon(), tripPointDTO.getElat(), tripPointDTO.getEname());

        return GeometryUtils.calculateFare(startPoint, endPoint);
    }


    public ResponseEntity<String> passengerRequest(TripRequestDTO tripRequestDTO) {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Passenger passenger = passengerRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Passenger not found."));
        checkUserStatus(passenger.getStatus(), UserStatus.NONE, "Passenger already has a trip process.");

        // vehicle type
        VehicleType vt = vehicleTypeRepository.findByName(tripRequestDTO.getVehicle_type()).orElseThrow( () -> new CustomResourceNotFoundException("Vehicle type not found."));
        
        // points
        TripPointDTO tripPointDTO = tripRequestDTO.getTripPoint();
        Point sPoint = getPoint(tripPointDTO.getSlon(), tripPointDTO.getSlat(), tripPointDTO.getSname());
        Point ePoint = getPoint(tripPointDTO.getElon(), tripPointDTO.getElat(), tripPointDTO.getEname());
        TripRequest tripRequest = TripRequest.builder().passenger(passenger).vehicleType(vt).fare(tripRequestDTO.getFare()).startPoint(sPoint).endPoint(ePoint).build();
        tripRequestRepository.save(tripRequest);

        return ResponseEntity.ok().body("Trip request has been saved.");
        
    }


    // util methods
    private Point getPoint(Double lon, Double lat, String name) {
        
        Point point;

        if (name != null) {
            point = passengerService.getPersonalLocationPoint(name);
        } else if (lat != null && lon != null) {
            point = geometryFactory.createPoint(new Coordinate(lon, lat));
        } else {
            throw new CustomBadRequestException("Invalid point parameters.");
        }

        return point;
    }

    private void checkUserStatus(UserStatus userStatus, UserStatus desiredStatus, String msg) {

        if (userStatus != desiredStatus) {
            throw new CustomBadRequestException(msg);
        }

    }
}
