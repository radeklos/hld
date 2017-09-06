package com.caribou.company.rest.dto;

import com.caribou.company.domain.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID uid;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;

    @JsonProperty
    private LocalDate startedAt;

}
