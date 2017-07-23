package com.caribou.holiday.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.caribou.holiday.service.ical.VCalendar;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class ICalServiceTest extends IntegrationTests {

    @Autowired
    private ICalService iCalService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private Company company = Factory.company();

    private LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();
    private UserAccount approver;

    @Before
    public void setUp() throws Exception {
        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);
        approver = userRepository.save(Factory.userAccount());
    }

    @Test
    public void getEventsOnlyForGivenUser() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(user, approver, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        UserAccount anotherUser = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(anotherUser, approver, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        VCalendar ical = iCalService.getCalendarForUser(user);
        assertThat(ical.getVEvents()).hasSize(1);
    }

    @Test
    public void wholeDayLeavesAreConvertedAsDate() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(
                user,
                approver,
                leaveType,
                LocalDateTime.of(2017, 5, 12, 0, 0, 0),
                LocalDateTime.of(2017, 5, 14, 0, 0, 0))
        );

        VCalendar ical = iCalService.getCalendarForUser(user);
        assertThat(ical.toICal()).contains("VALUE=DATE:20170512").contains("VALUE=DATE:20170514");
    }

    @Test
    public void partialDayLeavesAreConvertedAsZonedDateTime() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(
                user,
                approver,
                leaveType,
                LocalDateTime.of(2017, 5, 12, 12, 0, 0),
                LocalDateTime.of(2017, 5, 14, 12, 0, 0))
        );

        VCalendar ical = iCalService.getCalendarForUser(user);
        assertThat(ical.toICal()).contains("TZID=Europe/Prague:20170512T120000").contains("TZID=Europe/Prague:20170514T120000");
    }
}
