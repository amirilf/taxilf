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
public class DriverStatusDTO {

    String info;
    String status; // TODO
    Double fare;
    Point start_point;
    Point end_point;
    Point current_location;
    String passenger_name;
    String passenger_phone;
    Integer estimated_time; // TODO

}
