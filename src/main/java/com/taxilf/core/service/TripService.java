package com.taxilf.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.taxilf.core.exception.CustomBadRequestException;
import com.taxilf.core.exception.CustomConflictException;
import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.dto.request.PointDTO;
import com.taxilf.core.model.dto.request.TripPointDTO;
import com.taxilf.core.model.dto.request.TripRequestDTO;
import com.taxilf.core.model.dto.response.DriverSearchDTO;
import com.taxilf.core.model.dto.response.DriverStatusDTO;
import com.taxilf.core.model.dto.response.DriverTripRequestDTO;
import com.taxilf.core.model.dto.response.PassengerStatusDTO;
import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.Trip;
import com.taxilf.core.model.entity.TripRequest;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.enums.Role;
import com.taxilf.core.model.enums.TripRequestStatus;
import com.taxilf.core.model.enums.TripStatus;
import com.taxilf.core.model.enums.UserStatus;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.TripRepository;
import com.taxilf.core.model.repository.TripRequestRepository;
import com.taxilf.core.model.repository.UserRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;
import com.taxilf.core.utility.GeometryUtils;
import com.taxilf.core.utility.Variables;

@Service
public class TripService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final PassengerService passengerService;
    private final TripRepository tripRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final TripRequestRepository tripRequestRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(
        UserRepository userRepository,
        PassengerService passengerService, 
        PassengerRepository passengerRepository, 
        DriverRepository driverRepository,
        TripRepository tripRepository,
        VehicleTypeRepository vehicleTypeRepository, 
        TripRequestRepository tripRequestRepository
    ) {
        this.userRepository = userRepository;
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

    public ResponseEntity<String> updateLocation(PointDTO point) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String id = auth.getName();

        User user;

        if (role.equals(Role.PASSENGER.name())) {
            Passenger passenger = passengerRepository.findById(Long.parseLong(id)).orElseThrow(() -> new CustomResourceNotFoundException("Passenger not found."));
            user = passenger.getUser();
        } else if (role.equals(Role.DRIVER.name())) {
            Driver driver = driverRepository.findById(Long.parseLong(id)).orElseThrow(() -> new CustomResourceNotFoundException("Driver not found."));
            user = driver.getUser();
        } else {
            throw new CustomResourceNotFoundException("Not found.");
        }

        user.setLocation(geometryFactory.createPoint(new Coordinate(point.getLon(), point.getLat())));
        userRepository.save(user);
        return ResponseEntity.ok().body("Location updated.");

    }


    // PASSENGER
    public ResponseEntity<String> passengerRequest(TripRequestDTO tripRequestDTO) {

        Passenger passenger = getPassenger();
        User uPassenger = passenger.getUser();
        UserStatus pStatus = uPassenger.getStatus();
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
        uPassenger.setStatus(UserStatus.SEARCHING);
        userRepository.save(uPassenger);

        return ResponseEntity.ok().body("Trip request has been saved.");
    }

    public PassengerStatusDTO passengerStatus() {
        
        Passenger passenger = getPassenger();
        User uPassenger = passenger.getUser();
        UserStatus pStatus = uPassenger.getStatus();
        
        if (pStatus == UserStatus.NONE) {
            return PassengerStatusDTO.builder().info("You don't have any trip process.").build();
        } else if (pStatus == UserStatus.SEARCHING) {
            
            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(passenger.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            return PassengerStatusDTO.builder()
                .info("Searching for a driver.")
                .fare(tripRequest.getFare())
                .start_point(tripRequest.getStartPoint())
                .end_point(tripRequest.getEndPoint())
                .vehicle_type(tripRequest.getVehicleType().getName())
                .build();

        } else { // ACTIVE

            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(passenger.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            Trip trip = tripRequest.getTrip();
            User uDriver = trip.getDriver().getUser();

            return PassengerStatusDTO.builder()
                .info(trip.getStatus() == TripStatus.WAITING ? "Driver is comming." : "On going to destination.")
                .fare(tripRequest.getFare())
                .start_point(tripRequest.getStartPoint())
                .end_point(tripRequest.getEndPoint())
                .vehicle_type(tripRequest.getVehicleType().getName())
                .driver_name(uDriver.getName())
                .driver_phone(uDriver.getPhone())
                .driver_location(uDriver.getLocation())
                .build();
        }
    }

    public ResponseEntity<String> passengerCancel() {

        Passenger passenger = getPassenger();
        User uPassenger = passenger.getUser();
        UserStatus pStatus = uPassenger.getStatus();
        checkUserStatus(pStatus, "Passenger doesn't have a trip process to cancel.", UserStatus.SEARCHING, UserStatus.ACTIVE);

        if (pStatus == UserStatus.SEARCHING) {
            
            TripRequest tripRequest = tripRequestRepository.findPendingTripRequestByPassengerId(passenger.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            tripRequest.setStatus(TripRequestStatus.CANCELED);
            tripRequestRepository.save(tripRequest);

            uPassenger.setStatus(UserStatus.NONE);
            userRepository.save(uPassenger);

            return ResponseEntity.ok().body("Trip request has been successfully canceled.");

        } else { // ACTIVE

            TripRequest tripRequest = tripRequestRepository.findLastFoundedTripRequestByPassengerId(passenger.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
            Trip trip = tripRequest.getTrip();
            TripStatus tripStatus = trip.getStatus();
            User uDriver = trip.getDriver().getUser();

            if (tripStatus == TripStatus.WAITING) {

                uPassenger.setStatus(UserStatus.NONE);
                passengerRepository.save(passenger);

                uDriver.setStatus(UserStatus.SEARCHING);
                userRepository.save(uDriver);

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

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver already has a trip process.", UserStatus.NONE);

        uDriver.setStatus(UserStatus.SEARCHING);
        userRepository.save(uDriver);

        return getDriverSearchDTO(uDriver.getLocation());
    }

    public DriverSearchDTO driverSearch(){
        
        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "The driver is not in searching status.", UserStatus.SEARCHING);
        return getDriverSearchDTO(uDriver.getLocation());

    }

    public ResponseEntity<String> driverPick(Long tripRequestID){

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "The driver is not in searching status.", UserStatus.SEARCHING);

        TripRequest tr = tripRequestRepository.findById(tripRequestID).orElseThrow( () -> new CustomResourceNotFoundException("TripRequest not found."));
        TripRequestStatus trStatus = tr.getStatus();
        
        if (trStatus == TripRequestStatus.CANCELED) {
            throw new CustomResourceNotFoundException("TripRequest not found.");
        } else if (trStatus == TripRequestStatus.FOUND) {
            throw new CustomConflictException("TripRequest already taken by other driver.");
        }

        // create trip obj
        Trip trip = Trip.builder().driver(driver).tripRequest(tr).build();
        
        // update tripRequest obj
        tr.setStatus(TripRequestStatus.FOUND);
        
        // update passenger status
        User uPassenger = tr.getPassenger().getUser();
        uPassenger.setStatus(UserStatus.ACTIVE);

        // update driver status
        uDriver.setStatus(UserStatus.ACTIVE);

        // save updates
        tripRequestRepository.save(tr);
        tripRepository.save(trip);
        userRepository.save(uPassenger);
        userRepository.save(uDriver);

        return ResponseEntity.ok().body("Driver has successfully accepted the travel request");
    }

    public DriverStatusDTO driverStatus(){

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();

        if (dStatus == UserStatus.NONE) {
            return DriverStatusDTO.builder().info("Vakhe ye kari kon yare.").build();
        } else if (dStatus == UserStatus.SEARCHING) {
            return DriverStatusDTO.builder().info("Searching for trip requests").build();
        } else { // ACTIVE

            Trip trip = getLastTripByDriverID(driver.getId());
            TripStatus tripStatus = trip.getStatus();
            String info;

            if (tripStatus == TripStatus.WAITING) {
                info = "Driver is going to pick up the passenger.";
            } else if (tripStatus == TripStatus.ON_GOING) {
                info = "Driver is going to destination.";
            } else {
                throw new CustomResourceNotFoundException("No in-progress trip found.");
            }
            
            TripRequest tr = trip.getTripRequest();
            User uPassenger = tr.getPassenger().getUser();

            return DriverStatusDTO.builder()
                .info(info)
                .status(trip.getStatus().name())
                .fare(tr.getFare())
                .start_point(tr.getStartPoint())
                .end_point(tr.getEndPoint())
                .current_location(uDriver.getLocation())
                .passenger_name(uPassenger.getName())
                .passenger_phone(uPassenger.getPhone())
                .build();
        }
    }

    public ResponseEntity<String> driverCancel() {

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver doesn't have a trip process to cancel.", UserStatus.SEARCHING, UserStatus.ACTIVE);

        if (dStatus == UserStatus.SEARCHING) {
            
            uDriver.setStatus(UserStatus.NONE);
            userRepository.save(uDriver);
            return ResponseEntity.ok().body("Trip request has been successfully canceled.");

        } else { // ACTIVE

            // get trip to make cancel by driver
            // get passenger to update to Searching again
            // make himself Searching
            // lock!

            Trip trip = getLastTripByDriverID(driver.getId());
            User uPassenger = trip.getTripRequest().getPassenger().getUser();

            trip.setStatus(TripStatus.CANCELED_BY_DRIVER);
            uPassenger.setStatus(UserStatus.SEARCHING);
            uDriver.setStatus(UserStatus.NONE);

            userRepository.save(uPassenger);
            userRepository.save(uDriver);
            tripRepository.save(trip);

            return ResponseEntity.ok().body("Trip has been successfully canceled.");
        }

    }

    public ResponseEntity<String> driverOnBoard(){
        
        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver has no trip.", UserStatus.ACTIVE);

        Trip trip = getLastTripByDriverID(driver.getId());
        
        trip.setStatus(TripStatus.ON_GOING);
        trip.setStartTime(LocalDateTime.now());

        tripRepository.save(trip);

        return ResponseEntity.ok().body("The driver successfully picked up the passenger");        

    }

    public ResponseEntity<String> driverDone(){

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver has no trip.", UserStatus.ACTIVE);

        Trip trip = getLastTripByDriverID(driver.getId());
        TripRequest tripRequest = trip.getTripRequest();
        User uPassenger = tripRequest.getPassenger().getUser();
        
        // if (false) {
        //    checking that if the location of driver is near to the dest location or not /:
        //    and also check for passenger confirm
        // }

        uPassenger.setStatus(UserStatus.NONE);
        uDriver.setStatus(UserStatus.NONE);
        uDriver.setLocation(tripRequest.getEndPoint());
        trip.setEndTime(LocalDateTime.now());
        trip.setStatus(TripStatus.COMPLETED);

        userRepository.save(uPassenger);
        userRepository.save(uDriver);
        tripRepository.save(trip);

        return ResponseEntity.ok().body("Trip ended successfully.");

    }

    // util methods
    private Trip getLastTripByDriverID(Long id) {
        return tripRepository.findLastTripByDriverId(id).orElseThrow(() -> new CustomResourceNotFoundException("Trip not found."));
    }

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

    private Passenger getPassenger() {
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return passengerRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Passenger not found."));
    }

    private Driver getDriver() {
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return driverRepository.findById(id).orElseThrow( () -> new CustomResourceNotFoundException("Driver not found."));
    }

}
