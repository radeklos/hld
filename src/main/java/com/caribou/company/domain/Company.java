package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    private Integer defaultDaysOff;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyEmployee> employees;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<Department> departments;

    public Company() {
    }

    public Company(String name, Integer defaultDaysOff) {
        this.name = name;
        this.defaultDaysOff = defaultDaysOff;
    }

    private Company(Builder builder) {
        setName(builder.name);
        setDefaultDaysOff(builder.defaultDaysOff);
        employees = builder.employees;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Set<Department> getDepartments() {
        return departments;
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

    public void addEmployee(UserAccount userAccount, Role role) {
        if (employees == null) {
            employees = new HashSet<>();
        }
        employees.add(new CompanyEmployee(this, userAccount, role));
    }

    public void addDepartment(Department department) {
        if (departments == null) {
            departments = new HashSet<>();
        }
        departments.add(department);
    }

    public Set<CompanyEmployee> getEmployees() {
        return employees;
    }
    public static final class Builder {

        private String name;

        private Integer defaultDaysOff;
        private Set<CompanyEmployee> employees;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder defaultDaysOff(Integer val) {
            defaultDaysOff = val;
            return this;
        }

        public Builder employees(Set<CompanyEmployee> val) {
            employees = val;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
