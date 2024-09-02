package com.taxilf.core.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.VehicleType;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    
}
