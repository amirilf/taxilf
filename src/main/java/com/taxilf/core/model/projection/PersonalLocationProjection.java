package com.taxilf.core.model.projection;

import org.locationtech.jts.geom.Point;

public interface PersonalLocationProjection {

    String getName();
    Point getLocation();
    
}