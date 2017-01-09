package com.caribou.company.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;

import java.io.IOException;

public interface EmployeeParser {

    MappingIterator<? extends Row> read(String input) throws IOException;

    String generateExample() throws JsonProcessingException;

    interface Row {

        String getFirstName();

        String getLastName();

        String getEmail();

        String getDepartment();

        Double getReamingHoliday();

    }

}
