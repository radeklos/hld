package com.caribou;

import com.caribou.company.rest.map.EmployeeMap;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() throws IOException {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new EmployeeMap());
        return modelMapper;
    }

}
