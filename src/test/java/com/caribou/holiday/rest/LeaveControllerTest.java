package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
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
import rx.observers.TestSubscriber;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        userService.create(userAccount).subscribe(new TestSubscriber<>());

        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);
    }

    @Test
    public void create() throws Exception {
        LeaveDto leaveDto = LeaveDto.builder()
                .from(ZonedDateTime.now())
                .to(ZonedDateTime.now())
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
//        Leave leave = leaveRepository.findOne(response.getBody().getUid());
//        assertThat(leave.getUserAccount().getUid()).isEqualTo(userAccount.getUid());
    }

    @Test
    public void geListOfLeavesForUser() throws Exception {
        LocalDateTime now = LocalDateTime.of(2017, 1, 1, 12, 0, 0, 0);
        Leave leave1 = Leave.builder()
                .userAccount(userAccount)
                .reason("Holiday")
                .from(Timestamp.valueOf(now))
                .to(Timestamp.valueOf(now.plus(1, ChronoUnit.DAYS)))
                .leaveType(leaveType).build();
        Leave leave2 = Leave.builder()
                .userAccount(userAccount)
                .from(Timestamp.valueOf(now.plus(3, ChronoUnit.DAYS)))
                .to(Timestamp.valueOf(now.plus(5, ChronoUnit.DAYS)))
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
        assertThat(items.get(0).get("from")).isEqualTo("2017-01-01T12:00:00Z");
        assertThat(items.get(0).get("to")).isEqualTo("2017-01-02T12:00:00Z");
        assertThat(items.get(0).get("reason")).isEqualTo("Holiday");
    }

}
