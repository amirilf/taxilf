package com.taxilf.core.service;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.model.dto.request.FareDTO;
import com.taxilf.core.utility.GeometryUtils;

@Service
public class TripService {

    private final PassengerService passengerService;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public Double getFare(FareDTO fareDTO) {

        Double startPointLat = fareDTO.getSlat();
        Double startPointLon = fareDTO.getSlon();
        Double endPointLat = fareDTO.getElat();
        Double endPointLon = fareDTO.getElon(); // elon musk hahahhahahahahaha;
        String startPointName = fareDTO.getSname();
        String endPointName = fareDTO.getEname();

        Point startPoint;
        Point endPoint;

        if (startPointName != null) {
            startPoint = passengerService.getPersonalLocationPoint(startPointName);
        } else if (startPointLat != null && startPointLon != null) {
            startPoint = geometryFactory.createPoint(new Coordinate(startPointLon, startPointLat));
        } else {
            throw new CustomBadRequestException("Invalid start point parameters.");
        }

        if (endPointName != null) {
            endPoint = passengerService.getPersonalLocationPoint(endPointName);
        } else if (endPointLat != null && endPointLon != null) {
            endPoint = geometryFactory.createPoint(new Coordinate(endPointLon, endPointLat));
        } else {
            throw new CustomBadRequestException("Invalid end point parameters.");
        }

        return GeometryUtils.calculateFare(startPoint, endPoint);
    }

}
