package com.taxilf.core.model.dto.response;

import org.locationtech.jts.geom.Point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverTripRequestDTO {
    
    Long id;
    Point start_point;
    Point end_point;
    Double fare;

}
