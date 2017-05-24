package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;


@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_uid", "member_uid"})
)
@Entity
public class CompanyEmployee extends AbstractEntity {

    @ManyToOne(optional = false)
    private Company company;

    @ManyToOne(optional = false)
    private UserAccount member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public CompanyEmployee() {
    }

    CompanyEmployee(Company company, UserAccount member, Role role) {
        this.company = company;
        this.member = member;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public UserAccount getMember() {
        return member;
    }

}
