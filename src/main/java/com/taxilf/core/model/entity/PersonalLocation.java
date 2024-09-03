package com.taxilf.core.model.entity;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personal_locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    @JsonIgnore
    private Passenger passenger;

    @Column(name =  "name", unique = true)
    private String name;

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    @JsonIgnore
    private Point location;

}
