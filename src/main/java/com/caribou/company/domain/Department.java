package com.caribou.company.domain;

import com.caribou.AbstractEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.Set;


@Entity
public class Department extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer daysOff;

    @ManyToOne(optional = false)
    private Company company;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "department", fetch = FetchType.EAGER)
    private Set<DepartmentEmployee> employees;

    public Department() {
    }

    private Department(Builder builder) {
        name = builder.name;
        daysOff = builder.daysOff;
        this.setCompany(builder.company);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Set<DepartmentEmployee> getEmployees() {
        return Collections.unmodifiableSet(employees);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDaysOff() {
        return daysOff;
    }

    public void setDaysOff(Integer daysOff) {
        this.daysOff = daysOff;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        if (company == null) {
            return;
        }
        this.company = company;
        this.company.addDepartment(this);
    }

    public static final class Builder {

        private String name;

        private Integer daysOff;

        private Company company;

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

        public Builder company(Company val) {
            company = val;
            return this;
        }

        public Department build() {
            return new Department(this);
        }
    }

}
