package com.caribou.company.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CompanyDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long uid;

    @NotBlank
    @Size(max = 255)
    @JsonProperty
    private String name;

    @NotNull
    @Min(value = 0)
    @JsonProperty
    private Integer defaultDaysOff;

    public CompanyDto() {
    }

    private CompanyDto(Builder builder) {
        name = builder.name;
        defaultDaysOff = builder.defaultDaysOff;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefaultDaysOff() {
        return defaultDaysOff;
    }

    public void setDefaultDaysOff(Integer defaultDaysOff) {
        this.defaultDaysOff = defaultDaysOff;
    }

    public static final class Builder {

        private String name;

        private Integer defaultDaysOff;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder defaultDaysOf(Integer val) {
            defaultDaysOff = val;
            return this;
        }

        public CompanyDto build() {
            return new CompanyDto(this);
        }
    }
}
