package com.taxilf.core.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "vehicle_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "vehicleType")
    @JsonIgnore
    private List<VehicleSubtype> vehicleSubtypes;

    @OneToMany(mappedBy = "vehicleType", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TripRequest> tripRequests;

    @Column(name = "name", nullable = false, unique = true, updatable = false)
    private String name;

}
