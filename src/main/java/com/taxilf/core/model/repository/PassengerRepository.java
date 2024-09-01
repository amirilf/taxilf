package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Passenger;

import java.util.Optional;


@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    
    Optional<Passenger> findByPhone(String phone);
    boolean existsByPhone(String phone);

    @Query(value = "SELECT p.id FROM passengers p WHERE p.phone = :phone", nativeQuery = true)
    Long getIDByPhone(String phone); 
    
}
