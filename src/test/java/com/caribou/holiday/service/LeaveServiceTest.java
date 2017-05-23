package com.caribou.holiday.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rx.observers.TestSubscriber;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class LeaveServiceTest extends IntegrationTests {

    Company company = Factory.company();

    UserAccount userAccount = Factory.userAccount();

    LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveService leaveService;

    @Before
    public void setUp() throws Exception {
        companyRepository.save(company);
        userRepository.save(userAccount);
        leaveTypeRepository.save(leaveType);
    }

    @Test
    public void create() throws Exception {
        Leave leave = Leave.builder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .from(Timestamp.from(Instant.now()))
                .to(Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        Leave created = testSubscriber.getOnNextEvents().get(0);
        assertThat(created.getUid()).isNotNull();
        assertThat(created.getTo()).isEqualTo(leave.getTo());
        assertThat(created.getFrom()).isEqualTo(leave.getFrom());
        assertThat(created.getLeaveType()).isEqualTo(leave.getLeaveType());
        assertThat(created.getUserAccount()).isEqualTo(userAccount);
    }

}
