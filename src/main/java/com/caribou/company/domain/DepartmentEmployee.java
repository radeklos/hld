package com.caribou.company.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.*;
import java.math.BigDecimal;


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

    @Column(nullable = false)
    private BigDecimal remainingDaysOff;

    public DepartmentEmployee() {
    }

    public DepartmentEmployee(Department department, UserAccount member, BigDecimal remainingDaysOff, Role role) {
        this.department = department;
        this.member = member;
        this.role = role;
        this.remainingDaysOff = remainingDaysOff;
    }

    public UserAccount getMember() {
        return member;
    }

    public DepartmentEmployee setMember(UserAccount member) {
        this.member = member;
        return this;
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

    public DepartmentEmployee setDepartment(Department department) {
        this.department = department;
        return this;
    }

    public BigDecimal getRemainingDaysOff() {
        return remainingDaysOff;
    }

    public DepartmentEmployee setRemainingDaysOff(BigDecimal remainingDaysOff) {
        this.remainingDaysOff = remainingDaysOff;
        return this;
    }
}
