package com.taxilf.core.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.TripRequest;

@Repository
public interface TripRequestRepository extends JpaRepository<TripRequest, Long> {

    @Query(value = "SELECT * FROM trip_requests WHERE passenger_id = :passengerId AND status = 'PENDING' LIMIT 1", nativeQuery = true)
    Optional<TripRequest> findPendingTripRequestByPassengerId(Long passengerId);

    @Query(value = "SELECT * FROM trip_requests WHERE passenger_id = :passengerId AND status = 'FOUND' ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<TripRequest> findLastFoundedTripRequestByPassengerId(Long passengerId);

    @Query(value = "SELECT * FROM trip_requests " +
                   "WHERE status = 'PENDING' " +
                   "AND ST_DWithin(start_location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), :radius)", 
           nativeQuery = true)
    List<TripRequest> findPendingTripRequestsAroundLocation(double lon, double lat, double radius);
}
