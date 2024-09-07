package com.taxilf.core.service;

import org.springframework.stereotype.Service;

import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.entity.User;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class AdminService {
    
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    AdminService(PassengerRepository passengerRepository, DriverRepository driverRepository, UserRepository userRepository){
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    public List<Passenger> getPassengers() {
        return passengerRepository.findAll();
    }

    public List<Driver> getDrivers() {
        return driverRepository.findAll();
    }

    @Transactional
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
