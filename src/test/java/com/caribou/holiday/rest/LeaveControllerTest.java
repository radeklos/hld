package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.domain.When;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.caribou.holiday.rest.dto.LeaveDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.observers.TestSubscriber;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


public class LeaveControllerTest extends IntegrationTests {

    @Autowired
    UserService userService;

    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    Company company = Factory.company();

    LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    UserAccount userAccount;

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
        LeaveDto leaveDto = LeaveDto.newBuilder()
                .from(new Date())
                .leaveAt(When.Morning)
                .to(new Date())
                .returnAt(When.Evening)
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
        Leave leave = leaveRepository.findOne(response.getBody().getUid());
        assertThat(leave.getUserAccount().getUid()).isEqualTo(userAccount.getUid());
    }

}
