package com.taxilf.core.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "vehicle")
    @JsonIgnore
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_subtype_id", nullable = false)
    private VehicleSubtype vehicleSubtype;

    @Column(name = "model", nullable = false, updatable = false)
    private int model;

    @Column(name = "plate", nullable = false, unique = true, updatable = false)
    private String plate;

}
