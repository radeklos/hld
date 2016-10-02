package com.caribou.company.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class DepartmentDto {

    private Long uid;

    @NotBlank
    @Size(max = 255)
    @JsonProperty
    private String name;

    @NotNull
    @Min(value = 0)
    @JsonProperty
    private Integer daysOff;

    private DepartmentDto(Builder builder) {
        name = builder.name;
        daysOff = builder.daysOff;
    }

    public DepartmentDto() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Integer getDaysOff() {
        return daysOff;
    }

    public void setDaysOff(Integer daysOff) {
        this.daysOff = daysOff;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public static final class Builder {

        private String name;

        private Integer daysOff;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder daysOff(Integer val) {
            daysOff = val;
            return this;
        }

        public DepartmentDto build() {
            return new DepartmentDto(this);
        }
    }
}
