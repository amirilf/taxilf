package com.taxilf.core.model.dto.response;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence.Double;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerStatusDTO {
    
    String info;
    Double fare;
    Point start_point;
    Point end_point;
    String vehicle_type;
    String driver_name;
    String driver_phone;
    Point driver_location;
    Integer estimated_time;

}
