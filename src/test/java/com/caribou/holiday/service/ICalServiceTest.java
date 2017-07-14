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

    @Before
    public void setUp() throws Exception {
        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);
    }

    @Test
    public void getEventsOnlyForGivenUser() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(user, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        UserAccount anotherUser = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(anotherUser, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        VCalendar ical = iCalService.getCalendarForUser(user);
        assertThat(ical.getVEvents()).hasSize(1);
    }

}
