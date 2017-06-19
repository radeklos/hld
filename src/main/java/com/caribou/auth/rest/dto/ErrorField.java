package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorField {

    @JsonProperty
    String code;

    @JsonProperty
    String defaultMessage;

    @JsonProperty
    Object rejectedValue;

}
