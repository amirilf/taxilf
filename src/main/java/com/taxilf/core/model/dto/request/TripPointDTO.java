package com.taxilf.core.model.dto.request;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripPointDTO {
        
    @Range(min = -90, max = 90)
    Double slat;

    @Range(min = -180, max = 180)
    Double slon;

    @Range(min = -90, max = 90)
    Double elat;

    @Range(min = -180, max = 180)
    Double elon;
    
    String sname;
    String ename;

}
