package com.caribou.holiday.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rx.observers.TestSubscriber;

import java.time.LocalDate;
import java.util.List;

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
        Leave leave = Factory.leave(userAccount, leaveType);
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

    @Test
    public void getEmployeeLeaves() throws Exception {
        UserAccount emp1 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp1, Role.Viewer);
        leaveService.create(Factory.leave(emp1, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14))).subscribe(new TestSubscriber<>());

        UserAccount emp2 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp2, Role.Viewer);
        leaveService.create(Factory.leave(emp2, leaveType, LocalDate.of(2017, 5, 25), LocalDate.of(2017, 6, 14))).subscribe(new TestSubscriber<>());
        leaveService.create(Factory.leave(emp2, leaveType, LocalDate.of(2017, 6, 1), LocalDate.of(2017, 6, 14))).subscribe(new TestSubscriber<>());

        LocalDate from = LocalDate.of(2017, 5, 1);
        LocalDate to = LocalDate.of(2017, 5, 31);
        List<LeaveService.EmployeeLeaves> leaves = leaveService.getEmployeeLeaves(company.getUid().toString(), from, to).toList().toBlocking().first();

        assertThat(leaves).hasSize(2);
        assertThat(leaves.get(0).getLeaves()).hasSize(1);
        assertThat(leaves.get(1).getLeaves()).hasSize(1);
    }

}
