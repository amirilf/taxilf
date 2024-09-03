package com.taxilf.core.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.projection.PassengerProfileProjection;
import com.taxilf.core.model.repository.PassengerRepository;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    PassengerService(PassengerRepository passengerRepository){
        this.passengerRepository = passengerRepository;
    }
    
    public PassengerProfileProjection getProfile(){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return passengerRepository.findProfileById(id).orElseThrow(() -> new CustomResourceNotFoundException("Passenger not found."));
    }

}
