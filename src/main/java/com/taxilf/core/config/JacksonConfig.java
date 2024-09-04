package com.taxilf.core.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.taxilf.core.model.serialization.PointSerializer;
import org.locationtech.jts.geom.Point;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule customJacksonModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Point.class, new PointSerializer());
        return module;
    }
}
