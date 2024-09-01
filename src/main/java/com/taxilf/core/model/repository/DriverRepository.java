package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Driver;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByPhone(String phone);
    boolean existsByPhone(String phone);

    @Query(value = "SELECT d.id FROM drivers d WHERE d.phone = :phone", nativeQuery = true)
    Long getIDByPhone(String phone); 

}
