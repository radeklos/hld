package com.caribou.company.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class Department extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer daysOf;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Company company;

    public Department() {
    }

    private Department(Builder builder) {
        name = builder.name;
        daysOf = builder.daysOf;
        company = builder.company;
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

    public Integer getDaysOf() {
        return daysOf;
    }

    public void setDaysOf(Integer daysOf) {
        this.daysOf = daysOf;
    }

    public Company getCompany() {
        return company;
    }

    public static final class Builder {

        private String name;
        private Integer daysOf;
        private Company company;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder daysOf(Integer val) {
            daysOf = val;
            return this;
        }

        public Builder company(Company val) {
            company = val;
            return this;
        }

        public Department build() {
            return new Department(this);
        }
    }

}
