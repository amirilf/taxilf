package com.taxilf.core.model.dto.response;

import org.locationtech.jts.geom.Point;

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
public class TripPointsStatusDTO {
    
    Point driverLocation;
    Point passengerLocation;
    Point start;
    Point end;

}
