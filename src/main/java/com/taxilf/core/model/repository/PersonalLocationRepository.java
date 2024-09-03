package com.taxilf.core.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.PersonalLocation;
import com.taxilf.core.model.projection.PersonalLocationProjection;

@Repository
public interface PersonalLocationRepository extends JpaRepository<PersonalLocation, Long> {

    @Query("SELECT pl.name AS name, pl.location AS location " +
           "FROM PersonalLocation pl " +
           "WHERE pl.passenger.id = :passengerId")
    List<PersonalLocationProjection> findPersonalLocationsByPassengerId(Long passengerId);
    
}