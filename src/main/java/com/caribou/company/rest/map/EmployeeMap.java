package com.caribou.company.rest.map;

import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.rest.dto.EmployeeDto;
import org.modelmapper.PropertyMap;

public class EmployeeMap extends PropertyMap<DepartmentEmployee, EmployeeDto> {

    @Override
    protected void configure() {
        map().setEmail(source.getMember().getEmail());
        map().setFirstName(source.getMember().getFirstName());
        map().setLastName(source.getMember().getLastName());
        map().setRole(source.getRole());
        map().setUid(source.getMember().getUid());
    }

}
