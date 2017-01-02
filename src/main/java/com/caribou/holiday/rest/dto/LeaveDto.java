package com.caribou.holiday.rest.dto;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.domain.When;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;
import java.util.Date;


public class LeaveDto {

    @JsonProperty
    LeaveType leaveType;

    @JsonProperty(required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date from;

    @JsonProperty
    When leaveAt = When.Morning;

    @JsonProperty(required = true)
    @JsonFormat(pattern = "yyyy-MM-dd")
    Date to;

    @JsonProperty
    When returnAt = When.Evening;

    @Size(max = 255)
    @JsonProperty
    String reason;

    private Long uid;

    public LeaveDto() {
    }

    private LeaveDto(Builder builder) {
        setLeaveType(builder.leaveType);
        setFrom(builder.from);
        setLeaveAt(builder.leaveAt);
        setTo(builder.to);
        setReturnAt(builder.returnAt);
        setReason(builder.reason);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonProperty
    public Long getUid() {
        return uid;
    }

    public LeaveDto setUid(Long uid) {
        this.uid = uid;
        return this;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public LeaveDto setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
        return this;
    }

    public Date getFrom() {
        return from;
    }

    public LeaveDto setFrom(Date from) {
        this.from = from;
        return this;
    }

    public When getLeaveAt() {
        return leaveAt;
    }

    public LeaveDto setLeaveAt(When leaveAt) {
        this.leaveAt = leaveAt;
        return this;
    }

    public Date getTo() {
        return to;
    }

    public LeaveDto setTo(Date to) {
        this.to = to;
        return this;
    }

    public When getReturnAt() {
        return returnAt;
    }

    public LeaveDto setReturnAt(When returnAt) {
        this.returnAt = returnAt;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public LeaveDto setReason(String reason) {
        this.reason = reason;
        return this;
    }


    public static final class Builder {
        private LeaveType leaveType;
        private UserAccount userAccount;
        private Date from;
        private When leaveAt;
        private Date to;
        private When returnAt;
        private String reason;

        private Builder() {
        }

        public Builder leaveType(LeaveType val) {
            leaveType = val;
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

        public Builder leaveAt(When val) {
            leaveAt = val;
            return this;
        }

        public Builder to(Date val) {
            to = val;
            return this;
        }

        public Builder returnAt(When val) {
            returnAt = val;
            return this;
        }

        public Builder reason(String val) {
            reason = val;
            return this;
        }

        public LeaveDto build() {
            return new LeaveDto(this);
        }
    }
}
