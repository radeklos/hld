package com.caribou.company.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CompanyDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long uid;

    @NotEmpty
    @Size(max = 255)
    @JsonProperty
    private String name;

    @NotNull
    @Min(value = 0)
    @JsonProperty
    private Integer defaultDaysOf;

    public CompanyDto() {
    }

    private CompanyDto(Builder builder) {
        name = builder.name;
        defaultDaysOf = builder.defaultDaysOf;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultDaysOf() {
        return defaultDaysOf;
    }

    public void setDefaultDaysOf(Integer defaultDaysOf) {
        this.defaultDaysOf = defaultDaysOf;
    }

    public static final class Builder {

        private String name;
        private Integer defaultDaysOf;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder defaultDaysOf(Integer val) {
            defaultDaysOf = val;
            return this;
        }

        public CompanyDto build() {
            return new CompanyDto(this);
        }
    }
}
