package com.taxilf.core.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.PersonalLocation;
import com.taxilf.core.model.projection.PersonalLocationProjection;
import com.taxilf.core.model.projection.PointProjection;

@Repository
public interface PersonalLocationRepository extends JpaRepository<PersonalLocation, Long> {

    @Query(value = "SELECT * FROM personal_locations pl WHERE pl.passenger_id = :id AND name = :name", nativeQuery = true)
    Optional<PersonalLocation> findByPassengerIdAndName(Long id, String name);
   
    @Query(value = "SELECT ST_Y(pl.location) as lat, ST_X(pl.location) as lon " +
               "FROM personal_locations pl " +
               "WHERE pl.passenger_id = :id AND pl.name = :name", nativeQuery = true)
    Optional<PointProjection> findPointByPassengerIdAndName(Long id, String name);

    @Query("SELECT pl.name AS name, pl.location AS location " +
           "FROM PersonalLocation pl " +
           "WHERE pl.passenger.id = :passengerId")
    List<PersonalLocationProjection> findPersonalLocationsByPassengerId(Long passengerId);
    
}