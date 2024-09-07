package com.taxilf.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
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
import com.taxilf.core.model.entity.Transaction;
import com.taxilf.core.model.entity.Trip;
import com.taxilf.core.model.entity.TripRequest;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.entity.VehicleType;
import com.taxilf.core.model.entity.Wallet;
import com.taxilf.core.model.enums.TransactionStatus;
import com.taxilf.core.model.enums.TransactionType;
import com.taxilf.core.model.enums.TripRequestStatus;
import com.taxilf.core.model.enums.TripStatus;
import com.taxilf.core.model.enums.UserStatus;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.TransactionRepository;
import com.taxilf.core.model.repository.TripRepository;
import com.taxilf.core.model.repository.TripRequestRepository;
import com.taxilf.core.model.repository.UserRepository;
import com.taxilf.core.model.repository.VehicleTypeRepository;
import com.taxilf.core.model.repository.WalletRepository;
import com.taxilf.core.model.security.CustomUserPrincipal;
import com.taxilf.core.utility.GeometryUtils;
import com.taxilf.core.utility.Variables;

import jakarta.transaction.Transactional;

@Service
public class TripService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final PassengerService passengerService;
    private final TripRepository tripRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final TripRequestRepository tripRequestRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    TripService(
        UserRepository userRepository,
        PassengerService passengerService, 
        PassengerRepository passengerRepository, 
        DriverRepository driverRepository,
        TripRepository tripRepository,
        VehicleTypeRepository vehicleTypeRepository, 
        TripRequestRepository tripRequestRepository,
        TransactionRepository transactionRepository,
        WalletRepository walletRepository
    ) {
        this.userRepository = userRepository;
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.tripRepository = tripRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.tripRequestRepository = tripRequestRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Double getFare(TripPointDTO tripPointDTO) {

        Point startPoint = getPoint(tripPointDTO.getSlon(), tripPointDTO.getSlat(), tripPointDTO.getSname());
        Point endPoint = getPoint(tripPointDTO.getElon(), tripPointDTO.getElat(), tripPointDTO.getEname());

        return GeometryUtils.calculateFare(startPoint, endPoint);
    }

    public ResponseEntity<String> updateLocation(PointDTO point) {
        
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("User not found.")); 
        user.setLocation(geometryFactory.createPoint(new Coordinate(point.getLon(), point.getLat())));
        userRepository.save(user);
        return ResponseEntity.ok().body("Location updated.");

    }


    // PASSENGER

    @Transactional
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

    @Transactional
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
                return ResponseEntity.badRequest().body("Passenger can not cancel the trip.");
            }
        }
    }

    @Transactional
    public ResponseEntity<String> passengerPay() {

        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User uPassenger = userRepository.findById(cup.getUserId()).orElseThrow(() -> new CustomResourceNotFoundException("User not found."));
        UserStatus pStatus = uPassenger.getStatus();
        checkUserStatus(pStatus, "Passenger is not in a trip.", UserStatus.ACTIVE);

        TripRequest tripRequest = tripRequestRepository.findLastFoundedTripRequestByPassengerId(cup.getId()).orElseThrow(() -> new CustomResourceNotFoundException("Trip request not found."));
        Wallet wallet = uPassenger.getWallet();
        
        // redirect passenger to /payment/deposit to charge its wallet!
        Double fare = tripRequest.getFare();
        Double balance = wallet.getBalance();
        
        if (fare > balance) {
            Double need = fare - balance;
            System.out.println("this amount of money is needed: " + need);
            // redirect to payment with this amount of money
            // let's say it is done successfully
            wallet.setBalance(fare);
            walletRepository.save(wallet);
            return ResponseEntity.ok().body("The passenger's wallet has been successfully charged to the amount of the trip");
        } else {
            return ResponseEntity.ok().body("Passenger has enough money for this trip.");
        }
    }

    // DRIVER
    public DriverSearchDTO driverRequest() {

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver already has a trip process.", UserStatus.NONE);

        uDriver.setStatus(UserStatus.SEARCHING);
        userRepository.save(uDriver);

        return getDriverSearchDTO(uDriver.getLocation());
    }

    public DriverSearchDTO driverSearch() {
        
        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "The driver is not in searching status.", UserStatus.SEARCHING);
        return getDriverSearchDTO(uDriver.getLocation());

    }

    @Transactional
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

    public DriverStatusDTO driverStatus() {

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

    @Transactional
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

    public ResponseEntity<String> driverOnBoard() {
        
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

    @Transactional
    public ResponseEntity<String> driverDone() {

        Driver driver = getDriver();
        User uDriver = driver.getUser();
        UserStatus dStatus = uDriver.getStatus();
        checkUserStatus(dStatus, "Driver has no trip.", UserStatus.ACTIVE);

        Trip trip = getLastTripByDriverID(driver.getId());
        TripRequest tripRequest = trip.getTripRequest();
        User uPassenger = tripRequest.getPassenger().getUser();

        LocalDateTime now = LocalDateTime.now();
        
        // handle transactions
        Wallet dWallet = uDriver.getWallet();
        Wallet pWallet = uPassenger.getWallet();
        if (pWallet.getBalance() < tripRequest.getFare()) {
            // not enough money in passenger's wallet
            // in this state we send notif to driver app to say get money in cash
            // we wait for driver to call /confirm-cashe endpoint
            // then we add cash status transactions in that method
        } else {

            TransactionStatus ts = TransactionStatus.SUCCESS;
            TransactionType ttd  = TransactionType.DRIVER_TRIP_ADDITION;
            TransactionType ttp  = TransactionType.PASSENGER_TRIP_DEDUCTION;

            Transaction driverTransaction = Transaction.builder()
                .amount(tripRequest.getFare()) // we dont have any sood yet
                .status(ts)
                .type(ttd)
                .timestamp(now)
                .trip(trip)
                .build();

            Transaction passengerTransaction = Transaction.builder()
                .amount(tripRequest.getFare())
                .status(ts)
                .type(ttp)
                .timestamp(now)
                .trip(trip)
                .build();

            transactionRepository.save(driverTransaction);
            transactionRepository.save(passengerTransaction);
        }

        dWallet.setBalance(dWallet.getBalance() + tripRequest.getFare());
        pWallet.setBalance(pWallet.getBalance() - tripRequest.getFare());
        
        uPassenger.setStatus(UserStatus.NONE);
        uDriver.setStatus(UserStatus.NONE);
        
        uDriver.setLocation(tripRequest.getEndPoint());
        uPassenger.setLocation(tripRequest.getEndPoint());
        
        trip.setEndTime(now);
        trip.setStatus(TripStatus.COMPLETED);

        walletRepository.save(dWallet);
        walletRepository.save(pWallet);
        userRepository.save(uPassenger);
        userRepository.save(uDriver);
        tripRepository.save(trip);

        return ResponseEntity.ok().body("Trip ended successfully.");

    }

    public void driverCashConfirm() {
        // get driver confirmation and save transactions.
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
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getEntityById(cup.getId(), passengerRepository, "Passenger");
    }

    private Driver getDriver() {
        CustomUserPrincipal cup = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getEntityById(cup.getId(), driverRepository, "Driver");
    }

    private <T> T getEntityById(Long id, JpaRepository<T, Long> repository, String entityName) {
        return repository.findById(id).orElseThrow(() -> new CustomResourceNotFoundException(entityName + " not found."));
    }

}
