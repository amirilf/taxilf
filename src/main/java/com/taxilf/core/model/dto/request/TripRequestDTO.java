package com.taxilf.core.model.dto.request;

import org.locationtech.jts.geom.impl.PackedCoordinateSequence.Double;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {
    
    @NotEmpty
    private String vehicle_type;

    @NotNull
    @Positive
    private Double fare;

    private TripPointDTO tripPoint;

}
