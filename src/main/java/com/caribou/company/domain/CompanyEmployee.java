package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.BankHoliday;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;


@Data
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"company_uid", "member_uid"})
)
@Entity
public class CompanyEmployee extends AbstractEntity {

    @ManyToOne(optional = false)
    private Company company;

    @ManyToOne // (optional = false)
    private Department department;

    @ManyToOne(optional = false)
    private UserAccount member;

    @ManyToOne // when it's null approver is department boss
    private UserAccount approver;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private BankHoliday.Country location;

    @Column(nullable = false, columnDefinition = "Decimal(5,2) default '0.0'")
    private BigDecimal remainingDaysOff;

    public CompanyEmployee() {
        super();
    }

    CompanyEmployee(Company company, UserAccount member, Role role) {
        this.company = company;
        this.member = member;
        this.role = role;
    }

}
