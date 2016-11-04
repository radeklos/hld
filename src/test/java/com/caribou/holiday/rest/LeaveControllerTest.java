package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.WebApplication;
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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class LeaveControllerTest {

    private static TestRestTemplate restGuest = new TestRestTemplate();

    Faker faker = new Faker();

    @Autowired
    UserRepository userRepository;

    @Autowired
    LeaveRepository leaveRepository;

    Company company = Company.newBuilder().name(faker.company().name()).build();

    LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    @Value("${local.server.port}")
    private int port = 0;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private UserAccount userAccount;

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
        ResponseEntity<LeaveDto> response = new TestRestTemplate(userAccount.getEmail(), userAccount.getPassword()).exchange(
                path(url),
                HttpMethod.PUT,
                new HttpEntity<>(leaveDto, new HttpHeaders()),
                LeaveDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Leave leave = leaveRepository.findOne(response.getBody().getUid());
        assertThat(leave.getUserAccount().getUid()).as("Department isn't saved into company").isEqualTo(userAccount.getUid());
    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
