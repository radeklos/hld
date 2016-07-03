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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DepartmentEmployee that = (DepartmentEmployee) o;

        if (!department.equals(that.department)) return false;
        return member.equals(that.member);

    }

    @Override
    public int hashCode() {
        int result = department.hashCode();
        result = 31 * result + member.hashCode();
        return result;
    }
}
