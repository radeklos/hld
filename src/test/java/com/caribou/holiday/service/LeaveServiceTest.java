package com.caribou.holiday.service;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveTypeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.observers.TestSubscriber;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LeaveServiceTest {

    Company company = Company.newBuilder().name("company").build();

    UserAccount userAccount = UserAccount.newBuilder()
            .email("john.doe@email.com")
            .firstName("John")
            .lastName("Doe")
            .password("abcab")
            .build();

    LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    Department department = Department.newBuilder().company(company).daysOff(10).name("HR").build();


    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveService leaveService;

    @Before
    public void setUp() throws Exception {
        companyRepository.save(company);
        departmentRepository.save(department);
        userRepository.save(userAccount);
        leaveTypeRepository.save(leaveType);
    }

    @Test
    public void create() throws Exception {
        Leave leave = Leave.newBuilder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .department(department)
                .from(new Date())
                .to(new Date())
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        Leave created = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(created.getUid());
    }

    @Test
    public void fromCannotBeAfterTo() throws Exception {
        Leave leave = Leave.newBuilder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .department(department)
                .from(Date.from(LocalDate.of(2014, Month.DECEMBER, 12).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .to(Date.from(LocalDate.of(2014, Month.DECEMBER, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertError(ServiceValidationException.class);
    }

    @Test
    public void fromAndToCanBeSameDay() throws Exception {
        Leave leave = Leave.newBuilder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .department(department)
                .from(Date.from(LocalDate.of(2014, Month.DECEMBER, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .to(Date.from(LocalDate.of(2014, Month.DECEMBER, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
    }

    @Test
    public void cannotTakeTwoHalfDaysInOneDay() throws Exception {
        Leave leave = Leave.newBuilder()
                .userAccount(userAccount)
                .leaveType(leaveType)
                .department(department)
                .fromWholeDay(false)
                .toWholeDay(false)
                .from(Date.from(LocalDate.of(2014, Month.DECEMBER, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .to(Date.from(LocalDate.of(2014, Month.DECEMBER, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
        TestSubscriber<Leave> testSubscriber = new TestSubscriber<>();
        leaveService.create(leave).subscribe(testSubscriber);

        testSubscriber.assertError(ServiceValidationException.class);
    }

}
