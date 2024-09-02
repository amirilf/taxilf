package com.taxilf.core.service;

import org.springframework.stereotype.Service;

import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.repository.DriverRepository;
import com.taxilf.core.model.repository.PassengerRepository;

import java.util.List;

@Service
public class AdminService {
    
    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;

    AdminService(PassengerRepository passengerRepository, DriverRepository driverRepository){
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
    }

    public List<Passenger> getPassengers() {
        return passengerRepository.findAll();
    }

    public List<Driver> getDrivers(){
        return driverRepository.findAll();
    }

}
