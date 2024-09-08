package com.taxilf.core.service;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.dto.response.TripPointsStatusDTO;
import com.taxilf.core.model.entity.Trip;
import com.taxilf.core.model.entity.TripRequest;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.repository.TripRepository;
import com.taxilf.core.model.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class AdminService {
    
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    AdminService(UserRepository userRepository, TripRepository tripRepository){
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public TripPointsStatusDTO getUITripPoints(Long tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new CustomResourceNotFoundException("Trip not found."));
        TripRequest tr = trip.getTripRequest();
        
        Point driverLocation = trip.getDriver().getUser().getLocation();
        Point passengerLocation = tr.getPassenger().getUser().getLocation();
        Point start = tr.getStartPoint();
        Point end = tr.getEndPoint();

        return TripPointsStatusDTO.builder()
            .driverLocation(driverLocation)
            .passengerLocation(passengerLocation)
            .start(start)
            .end(end)
            .build();
    }

    public List<Trip> getTrips() {
        List<Trip> trips = tripRepository.findAll();
        return trips;
    }

}
