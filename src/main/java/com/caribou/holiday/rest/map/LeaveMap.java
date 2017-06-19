package com.caribou.holiday.rest.map;

import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.rest.dto.LeaveDto;
import org.modelmapper.PropertyMap;


public class LeaveMap extends PropertyMap<Leave, LeaveDto> {
    @Override
    protected void configure() {
        map().setReason(source.getReason());
        map().setLeaveType(source.getLeaveType());
        map().setFrom(source.getFrom().toLocalDate());
        map().setTo(source.getTo().toLocalDate());
    }
}
