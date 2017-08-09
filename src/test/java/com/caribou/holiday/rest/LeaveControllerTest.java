package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.caribou.holiday.rest.dto.LeaveDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class LeaveControllerTest extends IntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private Company company = Factory.company();

    private LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    private UserAccount userAccount;

    private String password;

    @Before
    public void before() throws Exception {
        userAccount = Factory.userAccount();
        password = userAccount.getPassword();
        userService.create(userAccount);

        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);

        UserAccount approver = userService.create(Factory.userAccount());
        Department department = departmentRepository.save(Factory.department(company, approver));
        companyRepository.addEmployee(company, department, userAccount, approver, Role.Viewer);
    }

    @Test
    public void create() throws Exception {
        LocalDate now = LocalDate.of(2017, 1, 1);
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(now)
                .ending(now.plus(7, ChronoUnit.DAYS))
                .startingAt(LeaveDto.AMPM.PM)
                .endingAt(LeaveDto.AMPM.PM)
                .build();
        String url = String.format("/v1/users/%s/leaves", userAccount.getUid());
        ResponseEntity<LeaveDto> response = post(
                url,
                leaveDto,
                LeaveDto.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        LeaveDto created = response.getBody();
        assertThat(created.getUid()).isNotNull();
        assertThat(created.getStarting()).isEqualTo(leaveDto.getStarting());
        assertThat(created.getEnding()).isEqualTo(leaveDto.getEnding());
        assertThat(created.getLeaveType()).isEqualTo(leaveDto.getLeaveType());

        Leave leave = leaveRepository.findOne(UUID.fromString(created.getUid()));
        assertThat(leave.getUserAccount()).isEqualTo(userAccount);
    }

    @Test
    public void returns404ForNonExistingUser() throws Exception {
        LocalDate now = LocalDate.of(2017, 1, 1);
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(now)
                .ending(now.plus(7, ChronoUnit.DAYS))
                .startingAt(LeaveDto.AMPM.PM)
                .endingAt(LeaveDto.AMPM.PM)
                .build();
        String url = String.format("/v1/users/%s/leaves", 0);
        ResponseEntity<LeaveDto> response = post(
                url,
                leaveDto,
                LeaveDto.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updatingAnotherUserReturns404() throws Exception {
        UserAccount anotherUser = userService.create(Factory.userAccount());
        LocalDate now = LocalDate.of(2017, 1, 1);
        LeaveDto leaveDto = LeaveDto.builder()
                .starting(now)
                .ending(now.plus(7, ChronoUnit.DAYS))
                .startingAt(LeaveDto.AMPM.PM)
                .endingAt(LeaveDto.AMPM.PM)
                .build();
        String url = String.format("/v1/users/%s/leaves", anotherUser.getUid());
        ResponseEntity<LeaveDto> response = post(
                url,
                leaveDto,
                LeaveDto.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void geListOfLeavesForUser() throws Exception {
        LocalDateTime now = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
        Leave leave1 = Leave.builder()
                .userAccount(userAccount)
                .approver(userAccount)
                .reason("Holiday")
                .starting(Timestamp.valueOf(now))
                .ending(Timestamp.valueOf(now.plus(1, ChronoUnit.DAYS)))
                .numberOfDays(BigDecimal.ONE)
                .status(Leave.Status.APPROVED)
                .leaveType(leaveType).build();
        Leave leave2 = Leave.builder()
                .userAccount(userAccount)
                .approver(userAccount)
                .starting(Timestamp.valueOf(now.plus(3, ChronoUnit.DAYS)))
                .ending(Timestamp.valueOf(now.plus(5, ChronoUnit.DAYS)))
                .numberOfDays(BigDecimal.valueOf(2))
                .status(Leave.Status.PENDING)
                .leaveType(leaveType).build();
        leaveRepository.save(Arrays.asList(leave1, leave2));

        String url = String.format("/v1/users/%s/leaves", userAccount.getUid());
        ResponseEntity<HashMap> response = get(
                url,
                HashMap.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        HashMap body = response.getBody();
        List<HashMap> items = (List<HashMap>) body.get("items");
        assertThat(items).hasSize(2);
        assertThat(items.get(0).get("starting")).isEqualTo("2017-01-01");
        assertThat(items.get(0).get("ending")).isEqualTo("2017-01-02");
        assertThat(items.get(0).get("reason")).isEqualTo("Holiday");
    }


    @Test
    public void geListOfLeavesForColleague() throws Exception {
        UserAccount colleague = Factory.userAccount();
        userService.create(colleague);
        companyRepository.save(company);
        companyRepository.addEmployee(company, colleague, Role.Viewer);

        LocalDateTime now = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
        Leave leave1 = Leave.builder()
                .userAccount(userAccount)
                .approver(userAccount)
                .reason("Holiday")
                .starting(Timestamp.valueOf(now))
                .ending(Timestamp.valueOf(now.plus(1, ChronoUnit.DAYS)))
                .numberOfDays(BigDecimal.ONE)
                .status(Leave.Status.APPROVED)
                .leaveType(leaveType).build();
        Leave leave2 = Leave.builder()
                .userAccount(colleague)
                .approver(userAccount)
                .starting(Timestamp.valueOf(now.plus(3, ChronoUnit.DAYS)))
                .ending(Timestamp.valueOf(now.plus(5, ChronoUnit.DAYS)))
                .numberOfDays(BigDecimal.valueOf(2))
                .status(Leave.Status.PENDING)
                .leaveType(leaveType).build();
        leaveRepository.save(Arrays.asList(leave1, leave2));

        String url = String.format("/v1/users/%s/leaves", colleague.getUid());
        ResponseEntity<HashMap> response = get(
                url,
                HashMap.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        HashMap body = response.getBody();
        List<HashMap> items = (List<HashMap>) body.get("items");
        assertThat(items).hasSize(1);
        assertThat(items.get(0).get("starting")).isEqualTo("2017-01-04");
        assertThat(items.get(0).get("ending")).isEqualTo("2017-01-06");
        assertThat(items.get(0).get("reason")).isNull();
    }

    @Test
    public void cannotGetUsersLeavesFromAnotherCompany() throws Exception {
        UserAccount anotherUser = Factory.userAccount();
        userService.create(anotherUser);
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        companyRepository.addEmployee(anotherCompany, anotherUser, Role.Viewer);

        String url = String.format("/v1/users/%s/leaves", anotherUser.getUid());
        ResponseEntity<HashMap> response = get(
                url,
                HashMap.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
