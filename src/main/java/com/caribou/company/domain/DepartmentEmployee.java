package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;


@Entity
public class DepartmentEmployee extends AbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Department department;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private UserAccount member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public DepartmentEmployee() {
    }

    public DepartmentEmployee(Department department, UserAccount member, Role role) {
        this.department = department;
        this.member = member;
        this.role = role;
    }
}
