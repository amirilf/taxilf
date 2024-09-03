package com.taxilf.core.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.taxilf.core.exception.CustomResourceNotFoundException;
import com.taxilf.core.model.projection.PassengerProfileProjection;
import com.taxilf.core.model.projection.PersonalLocationProjection;
import com.taxilf.core.model.repository.PassengerRepository;
import com.taxilf.core.model.repository.PersonalLocationRepository;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PersonalLocationRepository personalLocationRepository;

    PassengerService(PassengerRepository passengerRepository, PersonalLocationRepository personalLocationRepository){
        this.passengerRepository = passengerRepository;
        this.personalLocationRepository = personalLocationRepository;
    }
    
    public PassengerProfileProjection getProfile(){
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return passengerRepository.findProfileById(id).orElseThrow(() -> new CustomResourceNotFoundException("Passenger not found."));
    }

    public List<PersonalLocationProjection> getPersonalLocations() {
        Long id = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return personalLocationRepository.findPersonalLocationsByPassengerId(id);
    }
}
