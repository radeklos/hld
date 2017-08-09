package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Company extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal defaultDaysOff;

    @Column(nullable = false)
    private String regNo;

    private String vatId;

    @Column(nullable = false)
    private boolean paysVat;

    @Column(nullable = false)
    private String address1;

    @Column
    private String address2;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "company", fetch = FetchType.EAGER)
    private Set<CompanyEmployee> employees;

    @OneToMany(cascade = CascadeType.MERGE, mappedBy = "company", fetch = FetchType.EAGER)
    private Set<Department> departments;

    private Company(Builder builder) {
        this();
        setName(builder.name);
        setDefaultDaysOff(builder.defaultDaysOff);
        setRegNo(builder.regNo);
        setVatId(builder.vatId);
        setPaysVat(builder.paysVat);
        setAddress1(builder.address1);
        setAddress2(builder.address2);
        setCity(builder.city);
        setPostCode(builder.postCode);
        setEmployees(builder.employees);
        setDepartments(builder.departments);
    }

    public Company() {
        super();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }

    public boolean isPaysVat() {
        return paysVat;
    }

    public void setPaysVat(boolean paysVat) {
        this.paysVat = paysVat;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Set<Department> getDepartments() {
        if (departments == null) {
            return new HashSet<>();
        }
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDefaultDaysOff() {
        return defaultDaysOff;
    }

    public void setDefaultDaysOff(BigDecimal defaultDaysOff) {
        this.defaultDaysOff = defaultDaysOff;
    }

    /**
     * Add employee should be in company repository
     */
    @Deprecated
    public void addEmployee(UserAccount userAccount, Role role) {
        if (employees == null) {
            employees = new HashSet<>();
        }
        CompanyEmployee companyEmployee = new CompanyEmployee(this, userAccount, role);
        companyEmployee.setRemainingAllowance(BigDecimal.ZERO);
        if (employees.contains(companyEmployee)) {
            for (CompanyEmployee f : employees) {
                if (f.equals(companyEmployee)) {
                    f.setRole(role);
                    break;
                }
            }
        } else {
            employees.add(companyEmployee);
        }
    }

    public Set<CompanyEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<CompanyEmployee> employees) {
        this.employees = employees;
    }

    public static final class Builder {
        private String name;
        private BigDecimal defaultDaysOff;
        private String regNo;
        private String vatId;
        private boolean paysVat;
        private String address1;
        private String address2;
        private String city;
        private String postCode;
        private Set<CompanyEmployee> employees;
        private Set<Department> departments;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder defaultDaysOff(BigDecimal val) {
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

        public Builder postCode(String val) {
            postCode = val;
            return this;
        }

        public Builder employees(Set<CompanyEmployee> val) {
            employees = val;
            return this;
        }

        public Builder departments(Set<Department> val) {
            departments = val;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
