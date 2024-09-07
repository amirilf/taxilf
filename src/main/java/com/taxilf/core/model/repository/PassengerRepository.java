package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.projection.PassengerProfileProjection;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    @Query(value = "SELECT p.id FROM passengers p WHERE p.user_id = :userId", nativeQuery = true)
    Long findIdByUserId(Long userId);

    @Query(value = "SELECT " +
            "u.name AS name, u.phone AS phone, u.joined_at AS joinedAt, u.gender AS gender " +
            "FROM passengers p " +
            "JOIN users u ON p.user_id = u.id " +
            "WHERE p.id = :id", 
        nativeQuery = true)
    Optional<PassengerProfileProjection> findProfileById(Long id);
}