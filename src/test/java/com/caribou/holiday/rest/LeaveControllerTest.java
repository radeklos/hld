package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.holiday.domain.Leave;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.domain.When;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.caribou.holiday.rest.dto.LeaveDto;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


public class LeaveControllerTest extends IntegrationTests {

    private static TestRestTemplate restGuest = new TestRestTemplate();

    Faker faker = new Faker();

    @Autowired
    UserRepository userRepository;

    @Autowired
    LeaveRepository leaveRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    LeaveTypeRepository leaveTypeRepository;

    Company company = Company.newBuilder().name(faker.company().name()).build();

    LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    UserAccount userAccount;

    @Before
    public void before() throws Exception {
        userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);

        restGuest.setRequestFactory(requestFactory);
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
                userAccount.getPassword()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Leave leave = leaveRepository.findOne(response.getBody().getUid());
        assertThat(leave.getUserAccount().getUid()).as("Department isn't saved into company").isEqualTo(userAccount.getUid());
    }

}
