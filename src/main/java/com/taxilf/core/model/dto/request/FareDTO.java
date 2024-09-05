package com.taxilf.core.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareDTO {
    
        Double slat;
        Double slon;
        Double elat;
        Double elon;
        String sname;
        String ename;

}
