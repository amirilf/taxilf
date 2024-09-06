package com.taxilf.core.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverSearchDTO {
    
    String info;
    Double radius;
    Integer number_of_requests;
    List<DriverTripRequestDTO> requests;

}
