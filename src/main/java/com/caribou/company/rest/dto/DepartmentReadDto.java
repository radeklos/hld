package com.caribou.company.rest.dto;

import com.caribou.auth.rest.dto.NestedSingleObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentReadDto extends ResourceSupport {

    private String uid;

    @NotBlank
    @Size(max = 255)
    @JsonProperty
    private String name;

    @NotNull
    @Min(value = 0)
    @JsonProperty
    private BigDecimal daysOff;

    @NotNull
    @JsonProperty
    private NestedSingleObject boss;

}
