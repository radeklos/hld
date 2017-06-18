package com.caribou.holiday.domain;

import com.caribou.AbstractEntity;
import com.caribou.auth.domain.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Date;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leave extends AbstractEntity {

    @ManyToOne
    LeaveType leaveType;

    @ManyToOne(optional = false)
    UserAccount userAccount;

    @Column(nullable = false, name = "_from")
    Date from;

    @Column(nullable = false, name = "_to")
    Date to;

    String reason;

}
