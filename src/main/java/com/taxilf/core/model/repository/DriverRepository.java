package com.taxilf.core.model.repository;

import org.locationtech.jts.geom.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taxilf.core.model.entity.Driver;
import com.taxilf.core.model.projection.DriverProfileProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    boolean existsByPhone(String phone);

    @Query(value = "SELECT d.id FROM drivers d WHERE d.phone = :phone", nativeQuery = true)
    Long findIdByPhone(String phone); 

    @Query(value = "SELECT " +
                "d.name AS name, d.phone AS phone, d.joined_at AS joinedAt, d.gender AS gender, " +
                "v.model AS vehicleModel, v.plate AS vehiclePlate, vs.name AS vehicleSubtypeName, vt.name AS vehicleTypeName " +
                "FROM drivers d " +
                "JOIN vehicles v ON d.vehicle_id = v.id " +
                "JOIN vehicle_subtypes vs ON v.vehicle_subtype_id = vs.id " +
                "JOIN vehicle_types vt ON vs.vehicle_type_id = vt.id " +
                "WHERE d.id = :id", 
           nativeQuery = true)
    Optional<DriverProfileProjection> findProfileById(Long id);

    
    List<Location> findLocations();
}
