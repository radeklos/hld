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

import java.util.Date;

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
        Leave leave = Leave.newBuilder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .from(new Date())
                .to(new Date())
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        Leave created = testSubscriber.getOnNextEvents().get(0);
        assertThat(created.getUid()).isNotNull();
    }

}
