package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
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
        return employees;
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

    public void addEmployee(UserAccount userAccount, Role role) {
        if (employees == null) {
            employees = new HashSet<>();
        }
        DepartmentEmployee departmentEmployee = new DepartmentEmployee(this, userAccount, role);
        if (employees.contains(departmentEmployee)) {
            for (Iterator<DepartmentEmployee> it = employees.iterator(); it.hasNext(); ) {
                DepartmentEmployee f = it.next();
                if (f.equals(departmentEmployee)) {
                    f.setRole(role);
                    break;
                }
            }
        } else {
            employees.add(departmentEmployee);
        }
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
