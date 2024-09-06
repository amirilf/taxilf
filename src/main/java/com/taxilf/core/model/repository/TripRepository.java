package com.taxilf.core.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    @Query(value = "SELECT t FROM Trip t WHERE t.driver.id = :driverId ORDER BY t.timestamp DESC")
    Optional<Trip> findLastTripByDriverId(Long driverId);
}
