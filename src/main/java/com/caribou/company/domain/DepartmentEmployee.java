package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;


@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"department_uid", "member_uid"})
)
@Entity
public class DepartmentEmployee extends AbstractEntity {

    @ManyToOne(optional = false)
    private Department department;

    @ManyToOne(optional = false)
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

    public UserAccount getMember() {
        return member;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

}
