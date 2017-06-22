package com.caribou.company.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_account_uid"})
)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation extends AbstractEntity {

    @Column(nullable = false)
    private String key;

    @ManyToOne(optional = false)
    private Company company;

    @ManyToOne(optional = false)
    private Department department;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    private UserAccount userAccount;

}
