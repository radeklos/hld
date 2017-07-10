package com.caribou.holiday.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;


@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leave extends AbstractEntity {

    @ManyToOne
    private LeaveType leaveType;

    @ManyToOne(optional = false)
    private UserAccount userAccount;

    @Column(nullable = false, name = "starting")
    private Timestamp starting;

    @Column(nullable = false, name = "ending")
    private Timestamp ending;

    @Column(nullable = false)
    private Double numberOfDays;

    private String reason;

}
