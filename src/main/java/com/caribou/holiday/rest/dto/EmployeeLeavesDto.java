package com.caribou.holiday.rest.dto;

import com.caribou.auth.rest.dto.UserAccountDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeavesDto {

    @JsonProperty
    private UserAccountDto employee;

    @JsonProperty(required = true)
    private List<LeaveDto> leaves;

}
