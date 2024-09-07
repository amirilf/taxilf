package com.taxilf.core.service;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.projection.PassengerProfileProjection;
import com.taxilf.core.model.projection.PersonalLocationProjection;
import com.taxilf.core.model.projection.PointProjection;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.PersonalLocationRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PersonalLocationRepository personalLocationRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    PassengerService(PassengerRepository passengerRepository, PersonalLocationRepository personalLocationRepository){
        this.passengerRepository = passengerRepository;
        this.personalLocationRepository = personalLocationRepository;
    }
    
    public PassengerProfileProjection getProfile(){
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return passengerRepository.findProfileById(cup.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Passenger not found."));
    }

    public List<PersonalLocationProjection> getPersonalLocations() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return personalLocationRepository.findPersonalLocationsByPassengerId(cup.getId());
    }

    public PointProjection getPersonalLocatoinPointProjection(String name) {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return personalLocationRepository.findPointByPassengerIdAndName(cup.getId(), name).orElseThrow( () -> new CustomResourceNotFoundException("Location name " + name + " not found!"));
    }

    public Point getPersonalLocationPoint(String name) {
        PointProjection pp = getPersonalLocatoinPointProjection(name);
        return geometryFactory.createPoint(new Coordinate(pp.getLon(), pp.getLat()));
    }
}
