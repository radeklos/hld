package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import lombok.Data;
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
import java.util.Date;


@Data
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_uid", "member_uid"})
)
@Entity
public class CompanyEmployee extends AbstractEntity {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne(optional = false)
    private Company company;

    @ManyToOne // (optional = false)
    private Department department;

    @ManyToOne(optional = false)
    private UserAccount member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public CompanyEmployee() {
        super();
    }

    CompanyEmployee(Company company, UserAccount member, Role role) {
        this.company = company;
        this.member = member;
        this.role = role;
    }

}
