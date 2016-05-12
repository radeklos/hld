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

    private Integer defaultDaysOf;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
    private Set<CompanyEmployee> employees;

    public Company() {
    }

    public Company(String name, Integer defaultDaysOf) {
        this.name = name;
        this.defaultDaysOf = defaultDaysOf;
    }

    private Company(Builder builder) {
        setName(builder.name);
        setDefaultDaysOf(builder.defaultDaysOf);
        employees = builder.employees;
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

    public void addEmployee(UserAccount userAccount, Role role) {
        if (employees == null) {
            employees = new HashSet<>();
        }
        employees.add(new CompanyEmployee(this, userAccount, role));
    }

    public Set<CompanyEmployee> getEmployees() {
        return employees;
    }

    public static final class Builder {

        private String name;
        private Integer defaultDaysOf;
        private Set<CompanyEmployee> employees;

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

        public Builder employees(Set<CompanyEmployee> val) {
            employees = val;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
