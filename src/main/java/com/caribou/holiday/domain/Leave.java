package com.caribou.holiday.domain;

import com.caribou.auth.domain.UserAccount;
import com.caribou.company.domain.Department;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Leave extends AbstractEntity {

    @ManyToOne(optional = false)
    LeaveType leaveType;

    @ManyToOne(optional = false)
    Department department;

    @ManyToOne(optional = false)
    UserAccount userAccount;

    @Column(nullable = false, name = "_from")
    Date from;

    @Column(nullable = false)
    Boolean fromWholeDay = true;

    @Column(nullable = false, name = "_to")
    Date to;

    @Column(nullable = false)
    Boolean toWholeDay = true;

    String reason;

    private Leave(Builder builder) {
        leaveType = builder.leaveType;
        department = builder.department;
        userAccount = builder.userAccount;
        from = builder.from;
        fromWholeDay = builder.fromWholeDay;
        to = builder.to;
        toWholeDay = builder.toWholeDay;
        reason = builder.reason;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public Boolean getFromWholeDay() {
        return fromWholeDay;
    }

    public Boolean getToWholeDay() {
        return toWholeDay;
    }

    public static final class Builder {
        private LeaveType leaveType;
        private Department department;
        private UserAccount userAccount;
        private Date from;
        private Boolean fromWholeDay = true;
        private Date to;
        private Boolean toWholeDay = true;
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

        public Builder fromWholeDay(Boolean val) {
            fromWholeDay = val;
            return this;
        }

        public Builder to(Date val) {
            to = val;
            return this;
        }

        public Builder toWholeDay(Boolean val) {
            toWholeDay = val;
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
