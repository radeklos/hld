package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;


@Entity
public class CompanyEmployee extends AbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Company company;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private UserAccount member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public CompanyEmployee() {
    }

    public CompanyEmployee(Company company, UserAccount member, Role role) {
        this.company = company;
        this.member = member;
        this.role = role;
    }
}
