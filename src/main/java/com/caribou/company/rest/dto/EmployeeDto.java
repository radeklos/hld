package com.caribou.company.rest.dto;

import com.caribou.company.domain.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
public class EmployeeDto {

    @JsonProperty
    private UUID uid;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String email;

    @JsonProperty
    private Role role;

    @JsonProperty
    private LocalDate startedAt;

    public EmployeeDto() {
    }
}
