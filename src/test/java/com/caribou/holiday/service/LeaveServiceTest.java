package com.caribou.holiday.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.BankHoliday;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.BankHolidayRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rx.observers.TestSubscriber;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Autowired
    BankHolidayRepository bankHolidayRepository;

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
        assertThat(created.getEnding()).isEqualTo(leave.getEnding());
        assertThat(created.getStarting()).isEqualTo(leave.getStarting());
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
        List<LeaveService.EmployeeLeaves> leaves = leaveService.getEmployeeLeaves(company.getUid().toString(), from, to);

        assertThat(leaves).hasSize(2);
        assertThat(leaves.get(0).getLeaves()).hasSize(1);
        assertThat(leaves.get(1).getLeaves()).hasSize(1);
    }


    @Test
    public void getAllEmployeesEvenThoughtHeDoesNotHaveAnyLeaves() throws Exception {
        UserAccount emp1 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp1, Role.Viewer);
        leaveService.create(Factory.leave(emp1, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14))).subscribe(new TestSubscriber<>());

        UserAccount emp2 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp2, Role.Viewer);

        LocalDate from = LocalDate.of(2017, 5, 1);
        LocalDate to = LocalDate.of(2017, 5, 31);
        List<LeaveService.EmployeeLeaves> leaves = leaveService.getEmployeeLeaves(company.getUid().toString(), from, to);

        assertThat(leaves).hasSize(2);
    }

    @Test
    public void calculateNumberOfBookedDaysWithHolidays() throws Exception {
        Date date = Date.valueOf(LocalDate.of(2017, 6, 13));
        BankHoliday bankHoliday = BankHoliday.builder()
                .country(BankHoliday.Country.CZ)
                .date(date)
                .build();
        bankHolidayRepository.save(bankHoliday);

        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDate.of(2017, 6, 12).atStartOfDay()))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 6, 14).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(2));
    }

    @Test
    public void calculateNumberOfBookedDaysDuringWeekend() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDate.of(2017, 7, 7).atStartOfDay()))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 7, 12).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(4));
    }

    @Test
    public void calculateNumberOfBookedDaysForWholeWeek() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDate.of(2017, 7, 10).atStartOfDay()))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 7, 16).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    public void calculateNumberOfBookedDaysForOneDay() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDate.of(2017, 7, 3).atStartOfDay()))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 7, 3).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(1));
    }

    @Test
    public void calculateNumberOfBookedDaysForTheFirstHalfOfDay() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDate.of(2017, 7, 3).atStartOfDay()))
                .ending(Timestamp.valueOf(LocalDateTime.of(2017, 7, 3, 12, 0)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(0.5));
    }

    @Test
    public void calculateNumberOfBookedDaysForTheSecondHalfOfDay() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDateTime.of(2017, 7, 3, 12, 0)))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 7, 3).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(0.5));
    }

    @Test
    public void calculateNumberOfBookedDaysForHalfOfDayDuringHoliday() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDateTime.of(2017, 7, 15, 12, 0)))
                .ending(Timestamp.valueOf(LocalDate.of(2017, 7, 15).atTime(LocalTime.MAX)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(0));
    }

    @Test
    public void calculateNumberOfBookedDaysForHalfOfDaysDuringWeekend() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDateTime.of(2017, 7, 13, 12, 0)))
                .ending(Timestamp.valueOf(LocalDateTime.of(2017, 7, 18, 12, 0)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(3));
    }

    @Test
    public void calculateNumberOfBookedDaysForHalfOfDaysDuringWeekend2() throws Exception {
        Leave leave = Leave.builder()
                .starting(Timestamp.valueOf(LocalDateTime.of(2017, 7, 14, 12, 0)))
                .ending(Timestamp.valueOf(LocalDateTime.of(2017, 7, 16, 12, 0)))
                .build();
        BigDecimal bookedDaysOff = leaveService.numberOfBookedDays(leave, BankHoliday.Country.CZ);
        assertThat(bookedDaysOff).isEqualByComparingTo(BigDecimal.valueOf(0.5));
    }

}
