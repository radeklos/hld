package com.caribou.holiday.domain;

import com.caribou.AbstractEntity;
import com.caribou.company.domain.Company;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Getter
@Entity
public class LeaveType extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false)
    private Company company;

    public LeaveType() {
    }

    private LeaveType(Builder builder) {
        name = builder.name;
        company = builder.company;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private Company company;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder company(Company val) {
            company = val;
            return this;
        }

        public LeaveType build() {
            return new LeaveType(this);
        }
    }
}
