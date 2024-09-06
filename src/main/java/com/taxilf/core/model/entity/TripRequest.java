package com.taxilf.core.model.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taxilf.core.model.enums.TripRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Default
    @OneToOne(mappedBy = "tripRequest")
    private Trip trip = null;

    @Column(name = "fare", nullable = false)
    private Double fare;

    @Column(name = "start_location", columnDefinition = "geometry(Point, 4326)")
    @JsonIgnore
    private Point startPoint;

    @Column(name = "end_location", columnDefinition = "geometry(Point, 4326)")
    @JsonIgnore
    private Point endPoint;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TripRequestStatus status = TripRequestStatus.PENDING;

}
