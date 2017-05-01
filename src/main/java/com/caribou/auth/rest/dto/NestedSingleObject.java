package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NestedSingleObject {

    @JsonProperty
    private Long uid;

    @JsonProperty
    private String uri;

    @JsonProperty
    private String href;

}
