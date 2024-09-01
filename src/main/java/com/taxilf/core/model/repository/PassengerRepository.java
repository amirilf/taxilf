package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByPhone(String phone);
}
