package com.caribou.company.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class CompanyDto extends ResourceSupport {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String uid;

    @NotBlank
    @Size(max = 255)
    @JsonProperty
    private String name;

    @NotNull
    @Min(value = 0)
    @JsonProperty
    private Integer defaultDaysOff;

    @NotBlank
    @JsonProperty
    private String regNo;

    @JsonProperty
    private String vatId;

    @JsonProperty
    private boolean paysVat;

    @NotBlank
    @JsonProperty
    private String address1;

    @JsonProperty
    private String address2;

    @NotBlank
    @JsonProperty
    private String city;

    @NotBlank
    @JsonProperty
    private String postcode;

    public CompanyDto() {
    }

    private CompanyDto(Builder builder) {
        setUid(builder.uid);
        setName(builder.name);
        setDefaultDaysOff(builder.defaultDaysOff);
        setRegNo(builder.regNo);
        setVatId(builder.vatId);
        setPaysVat(builder.paysVat);
        setAddress1(builder.address1);
        setAddress2(builder.address2);
        setCity(builder.city);
        setPostcode(builder.postcode);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getUid() {
        return uid;
    }

    public CompanyDto setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public CompanyDto setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getDefaultDaysOff() {
        return defaultDaysOff;
    }

    public CompanyDto setDefaultDaysOff(Integer defaultDaysOff) {
        this.defaultDaysOff = defaultDaysOff;
        return this;
    }

    public String getRegNo() {
        return regNo;
    }

    public CompanyDto setRegNo(String regNo) {
        this.regNo = regNo;
        return this;
    }

    public String getVatId() {
        return vatId;
    }

    public CompanyDto setVatId(String vatId) {
        this.vatId = vatId;
        return this;
    }

    public boolean isPaysVat() {
        return paysVat;
    }

    public CompanyDto setPaysVat(boolean paysVat) {
        this.paysVat = paysVat;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public CompanyDto setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public CompanyDto setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getCity() {
        return city;
    }

    public CompanyDto setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostcode() {
        return postcode;
    }

    public CompanyDto setPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public static final class Builder {
        private String uid;
        private String name;
        private Integer defaultDaysOff;
        private String regNo;
        private String vatId;
        private boolean paysVat;
        private String address1;
        private String address2;
        private String city;
        private String postcode;

        private Builder() {
        }

        public Builder uid(String val) {
            uid = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder defaultDaysOff(Integer val) {
            defaultDaysOff = val;
            return this;
        }

        public Builder regNo(String val) {
            regNo = val;
            return this;
        }

        public Builder vatId(String val) {
            vatId = val;
            return this;
        }

        public Builder paysVat(boolean val) {
            paysVat = val;
            return this;
        }

        public Builder address1(String val) {
            address1 = val;
            return this;
        }

        public Builder address2(String val) {
            address2 = val;
            return this;
        }

        public Builder city(String val) {
            city = val;
            return this;
        }

        public Builder postcode(String val) {
            postcode = val;
            return this;
        }

        public CompanyDto build() {
            return new CompanyDto(this);
        }
    }
}
