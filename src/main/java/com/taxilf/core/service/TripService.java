package com.taxilf.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import com.taxilf.core.model.dto.response.DriverSearchDTO;
import com.taxilf.core.model.dto.response.DriverTripRequestDTO;
import com.taxilf.core.model.dto.response.PassengerStatusDTO;
import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.Trip;
import com.taxilf.core.model.entity.TripRequest;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.enums.TripRequestStatus;
import com.taxilf.core.model.enums.TripStatus;
import com.taxilf.core.model.enums.UserStatus;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.TripRepository;
import com.taxilf.core.model.repository.TripRequestRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;
import com.taxilf.core.utility.GeometryUtils;
import com.taxilf.core.utility.Variables;

@Service
public class TripService {

    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final PassengerService passengerService;
    private final TripRepository tripRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final TripRequestRepository tripRequestRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(PassengerService passengerService, 
                PassengerRepository passengerRepository, 
                DriverRepository driverRepository,
                TripRepository tripRepository,
                VehicleTypeRepository vehicleTypeRepository, 
                TripRequestRepository tripRequestRepository
    ) {
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.tripRequestRepository = tripRequestRepository;
    }

    public Double getFare(TripPointDTO tripPointDTO) {

        Point startPoint = getPoint(tripPointDTO.getSlon(), tripPointDTO.getSlat(), tripPointDTO.getSname());
        Point endPoint = getPoint(tripPointDTO.getElon(), tripPointDTO.getElat(), tripPointDTO.getEname());

        return GeometryUtils.calculateFare(startPoint, endPoint);
    }

    // PASSENGER
    public ResponseEntity<String> passengerRequest(TripRequestDTO tripRequestDTO) {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Passenger passenger = passengerRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Passenger not found."));
        UserStatus pStatus = passenger.getStatus();
        checkUserStatus(pStatus, "Passenger already has a trip process.", UserStatus.NONE);

        // vehicle type
        VehicleType vt = vehicleTypeRepository.findByName(tripRequestDTO.getVehicle_type()).orElseThrow( () -> new CustomResourceNotFoundException("Vehicle type not found."));
        
        // points
        TripPointDTO tripPointDTO = tripRequestDTO.getTripPoint();
        Point sPoint = getPoint(tripPointDTO.getSlon(), tripPointDTO.getSlat(), tripPointDTO.getSname());
        Point ePoint = getPoint(tripPointDTO.getElon(), tripPointDTO.getElat(), tripPointDTO.getEname());
        TripRequest tripRequest = TripRequest.builder().passenger(passenger).vehicleType(vt).fare(tripRequestDTO.getFare()).startPoint(sPoint).endPoint(ePoint).build();
        tripRequestRepository.save(tripRequest);

        // change status
        passenger.setStatus(UserStatus.SEARCHING);
        passengerRepository.save(passenger);

        return ResponseEntity.ok().body("Trip request has been saved.");
        
    }

    public PassengerStatusDTO passengerStatus() {
        
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Passenger passenger = passengerRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Passenger not found."));
        UserStatus pStatus = passenger.getStatus();
        
        if (pStatus == UserStatus.NONE) {
            return PassengerStatusDTO.builder().info("You don't have any trip process.").build();
        } else if (pStatus == UserStatus.SEARCHING) {
            
            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(id).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            return PassengerStatusDTO.builder()
                .info("Searching for a driver.")
                .fare(tripRequest.getFare())
                .start_point(tripRequest.getStartPoint())
                .end_point(tripRequest.getEndPoint())
                .vehicle_type(tripRequest.getVehicleType().getName())
                .build();

        } else { // ACTIVE

            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(id).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            Trip trip = tripRequest.getTrip();
            Driver driver = trip.getDriver();

            return PassengerStatusDTO.builder()
                .info(trip.getStatus() == TripStatus.WAITING ? "Driver is comming." : "On going to destination.")
                .fare(tripRequest.getFare())
                .start_point(tripRequest.getStartPoint())
                .end_point(tripRequest.getEndPoint())
                .vehicle_type(tripRequest.getVehicleType().getName())
                .driver_name(driver.getName())
                .driver_phone(driver.getPhone())
                .driver_location(driver.getLocation())
                .build();
        }
    }

    public ResponseEntity<String> passengerCancel() {

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Passenger passenger = passengerRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Passenger not found."));
        UserStatus pStatus = passenger.getStatus();
        checkUserStatus(pStatus, "Passenger doesn't have a trip process to cancel.", UserStatus.SEARCHING, UserStatus.ACTIVE);

        if (pStatus == UserStatus.SEARCHING) {
            
            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(id).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            tripRequest.setStatus(TripRequestStatus.CANCELED);
            tripRequestRepository.save(tripRequest);

            passenger.setStatus(UserStatus.NONE);
            passengerRepository.save(passenger);

            return ResponseEntity.ok().body("Trip request has been successfully canceled.");

        } else { // ACTIVE

            TripRequest tripRequest = tripRequestRepository.findLastFoundedTripRequestByPassengerId(id).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            Trip trip = tripRequest.getTrip();
            TripStatus tripStatus = trip.getStatus();
            Driver driver = trip.getDriver();

            if (tripStatus == TripStatus.WAITING) {

                passenger.setStatus(UserStatus.NONE);
                passengerRepository.save(passenger);

                driver.setStatus(UserStatus.SEARCHING);
                driverRepository.save(driver);

                trip.setStatus(TripStatus.CANCELED_BY_PASSENGER);
                tripRepository.save(trip);

                return ResponseEntity.ok().body("Trip has been successfully canceled.");

            } else {
                return ResponseEntity.badRequest().body("Passenger is on way & can not cancel the trip.");
            }
        }
    }

    // DRIVER
    public DriverSearchDTO driverRequest(){

        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Driver driver = driverRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Driver not found."));
        UserStatus dStatus = driver.getStatus();
        checkUserStatus(dStatus, "Driver already has a trip process.", UserStatus.NONE);

        driver.setStatus(UserStatus.SEARCHING);
        driverRepository.save(driver);

        return getDriverSearchDTO(driver.getLocation());
    }

    public DriverSearchDTO driverSearch(){
        
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Driver driver = driverRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Driver not found."));
        UserStatus dStatus = driver.getStatus();
        checkUserStatus(dStatus, "The driver is not in searching status.", UserStatus.SEARCHING);
        return getDriverSearchDTO(driver.getLocation());

    }

    public void driverStatus(){

    }

    public void driverCancel(){

    }

    public void driverPick(){

    }

    public void driverOnBoard(){
        
    }

    public void driverDone(){

    }

    public void driverCasheConfirm(){

    }

    // util methods

    private DriverSearchDTO getDriverSearchDTO(Point point) {
        
        // retriving near triprequest.
        List<TripRequest> tripRequests = tripRequestRepository.findPendingTripRequestsAroundLocation(point.getX(), point.getY(), Variables.TRIP_RADIUS_KM); 
        
        return DriverSearchDTO.builder()
            .info("Searching for trip requests.")
            .number_of_requests(tripRequests.size())
            .radius(Variables.TRIP_RADIUS_KM)
            .requests(
                tripRequests.stream()
                .map((TripRequest tr) -> DriverTripRequestDTO.builder()
                    .id(tr.getId())
                    .start_point(tr.getStartPoint())
                    .end_point(tr.getEndPoint())
                    .fare(tr.getFare())
                    .build())
                .collect(Collectors.toList())
            )
            .build();
    }

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

    private void checkUserStatus(UserStatus currentStatus, String msg, UserStatus... desiredStatuses) {
        
        boolean match = Arrays.stream(desiredStatuses).anyMatch(status -> status == currentStatus);
        
        if (!match) {
            throw new CustomBadRequestException(msg);
        }
    }

}
