package com.caribou.holiday.domain;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Department;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Leave extends AbstractEntity {

    @ManyToOne
    LeaveType leaveType;

    @ManyToOne(optional = false)
    UserAccount userAccount;

    @Column(nullable = false, name = "_from")
    Date from;

    @Column(nullable = false)
    When leaveAt = When.Morning;

    @Column(nullable = false, name = "_to")
    Date to;

    @Column(nullable = false)
    When returnAt = When.Evening;

    String reason;

    public Leave() {
    }

    private Leave(Builder builder) {
        leaveType = builder.leaveType;
        userAccount = builder.userAccount;
        from = builder.from;
        to = builder.to;
        reason = builder.reason;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public Leave setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
        return this;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public Leave setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public When getLeaveAt() {
        return leaveAt;
    }

    public Leave setLeaveAt(When leaveAt) {
        this.leaveAt = leaveAt;
        return this;
    }

    public When getReturnAt() {
        return returnAt;
    }

    public Leave setReturnAt(When returnAt) {
        this.returnAt = returnAt;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Leave setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Date getFrom() {
        return from;
    }

    public Leave setFrom(Date from) {
        this.from = from;
        return this;
    }

    public Date getTo() {
        return to;
    }

    public Leave setTo(Date to) {
        this.to = to;
        return this;
    }

    public static final class Builder {
        private LeaveType leaveType;
        private Department department;
        private UserAccount userAccount;
        private Date from;
        private Date to;
        private String reason;

        private Builder() {
        }

        public Builder leaveType(LeaveType val) {
            leaveType = val;
            return this;
        }

        public Builder department(Department val) {
            department = val;
            return this;
        }

        public Builder userAccount(UserAccount val) {
            userAccount = val;
            return this;
        }

        public Builder from(Date val) {
            from = val;
            return this;
        }

        public Builder to(Date val) {
            to = val;
            return this;
        }

        public Builder reason(String val) {
            reason = val;
            return this;
        }

        public Leave build() {
            return new Leave(this);
        }
    }
}
