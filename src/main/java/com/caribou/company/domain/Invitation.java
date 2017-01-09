package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;
import java.util.UUID;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_account_uid"})
)
public class Invitation extends AbstractEntity {

    @Column(nullable = false)
    private String key;

    @ManyToOne(optional = false)
    private Company company;

    @ManyToOne(optional = false)
    private Department department;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private UserAccount userAccount;

    private Invitation(Builder builder) {
        this();
        company = builder.company;
        department = builder.department;
        userAccount = builder.userAccount;
    }

    public Invitation() {
        key = UUID.randomUUID().toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getKey() {
        return key;
    }

    public Company getCompany() {
        return company;
    }

    public Invitation setCompany(Company company) {
        this.company = company;
        return this;
    }

    public Department getDepartment() {
        return department;
    }

    public Invitation setDepartment(Department department) {
        this.department = department;
        return this;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Invitation setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public static final class Builder {
        private Company company;
        private Department department;
        private UserAccount userAccount;

        private Builder() {
        }

        public Builder company(Company val) {
            company = val;
            return this;
        }

        public Builder department(Department val) {
            department = val;
            return this;
        }

        public Builder userAccount(UserAccount val) {
            userAccount = val;
            return this;
        }

        public Invitation build() {
            return new Invitation(this);
        }
    }
}
