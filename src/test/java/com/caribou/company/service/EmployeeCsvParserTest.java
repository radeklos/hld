package com.caribou.company.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EmployeeCsvParserTest {

    EmployeeCsvParser employeeImporter = new EmployeeCsvParser();

    @Test
    public void parserCsv() throws Exception {
        String csv = "first name,last name,email,department,reaming holiday\n" +
                "john,doe,john.doe@missing.com,missing,5.23";

        MappingIterator<EmployeeCsvParser.Row> output = employeeImporter.read(csv);

        EmployeeCsvParser.Row employee = output.next();
        assertThat(employee.getFirstName()).isEqualTo("john");
        assertThat(employee.getLastName()).isEqualTo("doe");
        assertThat(employee.getEmail()).isEqualTo("john.doe@missing.com");
        assertThat(employee.getDepartment()).isEqualTo("missing");
        assertThat(employee.getReamingHoliday()).isEqualTo(5.23);
    }

    @Test(expected = RuntimeJsonMappingException.class)
    public void wrongNumberFormat() throws Exception {
        String csv = "first name,last name,email,department,reaming holiday\n" +
                "john,doe,john.doe@missing.com,missing,5,23";

        MappingIterator<EmployeeCsvParser.Row> output = employeeImporter.read(csv);
        output.next();
    }

    @Test(expected = RuntimeJsonMappingException.class)
    public void quotedWrongFormatOfNumber() throws Exception {
        String csv = "first name,last name,email,department,reaming holiday\n" +
                "john,doe,john.doe@missing.com,missing,\"5,23\"";

        MappingIterator<EmployeeCsvParser.Row> output = employeeImporter.read(csv);
        output.next();
    }

    @Test
    public void generateExample() throws Exception {
        String csv = employeeImporter.generateExample();
        assertThat(csv).isEqualTo("\"first name\",\"last name\",email,department,\"reaming holiday\"\n" +
                "Bernhard,Cummerata,\"Bernhard.Cummerata@email.com\",HR,24.5\n");
    }
}
