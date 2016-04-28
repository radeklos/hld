package com.caribou.holiday.service;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.github.javafaker.Faker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.observers.TestSubscriber;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class LeaveServiceTest {

    Faker faker = new Faker();

    Company company = Company.newBuilder().name(faker.company().name()).build();

    UserAccount userAccount = UserAccount.newBuilder()
            .email(faker.internet().emailAddress())
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .password(faker.internet().password())
            .build();

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
        Assert.assertNotNull(created.getUid());
    }

}
