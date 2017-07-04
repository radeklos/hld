package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Date;


@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"department_uid", "member_uid"})
)
@Entity
public class DepartmentEmployee extends AbstractEntity {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

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
        super();
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
