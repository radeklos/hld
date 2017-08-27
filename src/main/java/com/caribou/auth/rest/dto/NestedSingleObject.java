package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NestedSingleObject {

    @NotNull
    @NotBlank
    @JsonProperty
    private String uid;

    @Deprecated
    @JsonProperty
    private String uri;

    @JsonProperty
    private String label;

    @JsonProperty
    private String href;

}
