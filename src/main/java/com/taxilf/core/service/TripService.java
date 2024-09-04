package com.taxilf.core.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.entity.PersonalLocation;
import com.taxilf.core.model.repository.PersonalLocationRepository;
import com.taxilf.core.utility.GeometryUtils;

@Service
public class TripService {

    private final PersonalLocationRepository personalLocationRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(PersonalLocationRepository personalLocationRepository) {
        this.personalLocationRepository = personalLocationRepository;
    }

    public Double getFare(Double startPointLat, Double startPointLon, Double endPointLat, Double endPointLon, String startLocationName, String endLocationName) {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Point startPoint;
        Point endPoint;

        if (startPointLat != null && startPointLon != null) {
            
            if (startLocationName != null) {
                throw new CustomBadRequestException("Use one way to declare start point.");
            }
            startPoint = geometryFactory.createPoint(new Coordinate(startPointLon, startPointLat));
        
        } else if (startLocationName != null) {
            PersonalLocation pl = personalLocationRepository.findByPassengerIdAndName(id, startLocationName).orElseThrow( () -> new CustomResourceNotFoundException("Start location name is not found!"));
            startPoint = pl.getLocation();
        } else {
            throw new CustomBadRequestException("Invalid start point parameters.");
        }

        if (endPointLat != null && endPointLon != null) {
            
            if (endLocationName != null) {
                throw new CustomBadRequestException("Use one way to declare end point.");
            }
            endPoint = geometryFactory.createPoint(new Coordinate(endPointLon, endPointLat));
        
        } else if (endLocationName != null) {
            PersonalLocation pl = personalLocationRepository.findByPassengerIdAndName(id, endLocationName).orElseThrow( () -> new CustomResourceNotFoundException("End location name is not found!"));
            endPoint = pl.getLocation();
        } else {
            throw new CustomBadRequestException("Invalid end point parameters.");
        }

        return GeometryUtils.calculateFare(startPoint, endPoint);
    }

}
