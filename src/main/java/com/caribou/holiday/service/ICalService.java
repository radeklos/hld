package com.caribou.holiday.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.service.ical.VCalendar;
import com.caribou.holiday.service.ical.VEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;


@Service
public class ICalService {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Prague");

    private final LeaveRepository leaveRepository;

    @Autowired
    public ICalService(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    public VCalendar getCalendarForUser(UserAccount userAccount) {
        return builder(userAccount)
                .vEvents(leaveRepository.findByUserAccount(userAccount).stream().map(l -> map(userAccount, l)).collect(Collectors.toList()))
                .build();
    }

    private VCalendar.VCalendarBuilder builder(UserAccount userAccount) {
        return VCalendar.builder()
                .prodid("-//chll//leaves//EN")
                .xWrCalName("Chll - " + name(userAccount))
                .xPublishedTTL("PT1H")
                .xWrTimeZone(ZONE_ID.toString());
    }

    private VEvent map(UserAccount userAccount, Leave leave) {
        return VEvent.builder()
                .uid(leave.getUid() + "@chll.cz")
                .created(leave.getCreatedAt().toInstant())
                .lastModified(leave.getUpdatedAt().toInstant())
                .dtStart(leave.getStarting().toLocalDateTime().atZone(ZONE_ID))
                .dtEnd(leave.getEnding().toLocalDateTime().atZone(ZONE_ID))
                .dtstamp(Instant.now())
                .summary(name(userAccount) + ": " + (leave.getLeaveType() == null ? "Holiday" : leave.getLeaveType().getName()))
                .status(mapStatus(leave.getStatus()))
                .build();
    }

    private String name(UserAccount userAccount) {
        return userAccount.getFirstName() + " " + userAccount.getLastName();
    }

    private VEvent.Status mapStatus(Leave.Status status) {
        switch (status) {
            case PENDING:
                return VEvent.Status.TENTATIVE;
            case CONFIRMED:
                return VEvent.Status.CONFIRMED;
            default:
                return VEvent.Status.CANCELLED;
        }
    }

}
