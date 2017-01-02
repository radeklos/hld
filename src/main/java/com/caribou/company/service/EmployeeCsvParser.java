package com.caribou.company.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmployeeCsvParser {

    public MappingIterator<Row> read(String csv) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Row.class).withHeader();

        return mapper.readerFor(Row.class).with(schema).readValues(csv);
    }

    public String generateExample() throws JsonProcessingException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(Row.class).withHeader();
        return mapper.writer(schema).writeValueAsString(new Row(
                "Bernhard",
                "Cummerata",
                "Bernhard.Cummerata@email.com",
                "HR",
                24.5d
        ));
    }

    @JsonPropertyOrder(value = {"firstName", "lastName", "email", "department", "reamingHoliday"})
    public static class Row {

        @JsonProperty("reaming holiday")
        public Double reamingHoliday;

        @JsonProperty("first name")
        private String firstName;

        @JsonProperty("last name")
        private String lastName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("department")
        private String department;

        public Row() {
        }

        public Row(String firstName, String lastName, String email, String department, Double reamingHoliday) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.department = department;
            this.reamingHoliday = reamingHoliday;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public Double getReamingHoliday() {
            return reamingHoliday;
        }

        public void setReamingHoliday(Double reamingHoliday) {
            this.reamingHoliday = reamingHoliday;
        }
    }

}
