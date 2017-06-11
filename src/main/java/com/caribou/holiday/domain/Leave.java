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
import java.sql.Timestamp;


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
    Timestamp from;

    @Column(nullable = false, name = "_to")
    Timestamp to;

    String reason;

}
