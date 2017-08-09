package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Department extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal daysOff;

    @ManyToOne(optional = false)
    private UserAccount boss;

    @ManyToOne(optional = false)
    private Company company;

}
