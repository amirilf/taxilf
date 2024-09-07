package com.taxilf.core.model.dto.request;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointDTO {
        
    @Range(min = -90, max = 90)
    Double lat;

    @Range(min = -180, max = 180)
    Double lon;

}
