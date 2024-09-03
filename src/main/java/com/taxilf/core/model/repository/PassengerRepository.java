package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Passenger;
import com.taxilf.core.model.projection.PassengerProfileProjection;
import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    Optional<Passenger> findByPhone(String phone);
    boolean existsByPhone(String phone);

    @Query(value = "SELECT p.id FROM passengers p WHERE p.phone = :phone", nativeQuery = true)
    Long findIdByPhone(String phone);

    @Query(value = "SELECT " +
                   "p.name AS name, p.phone AS phone, p.joined_at AS joinedAt, p.gender AS gender " +
                   "FROM passengers p " +
                   "WHERE p.id = :id", 
           nativeQuery = true)
    Optional<PassengerProfileProjection> findProfileById(Long id);   

}