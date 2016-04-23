package com.caribou.controller.domain;

import com.caribou.auth.domain.UserAccount;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;


@Entity
public class Holiday extends AbstractEntity {

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private TypeLeave leaveTypeUrn;
    @Column(nullable = false)
    private String reason;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private UserAccount user;
    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false)
    private boolean halfStartDate;
    @Column(nullable = false)
    private boolean halfEndData;
    @Column(nullable = false)
    private boolean confirmed;
    private String bossResponse;

    private Holiday() {
    }

}
