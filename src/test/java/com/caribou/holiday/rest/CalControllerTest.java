package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveRepository;
import com.caribou.holiday.repository.LeaveTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class CalControllerTest extends IntegrationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private Company company = Factory.company();

    private LeaveType leaveType = LeaveType.newBuilder().company(company).name("Holiday").build();

    @Before
    public void setUp() throws Exception {
        companyRepository.save(company);
        leaveTypeRepository.save(leaveType);
    }

    @Test
    public void isPublic() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(user, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        ResponseEntity<String> response = get("/cal/" + user.getUid(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void notFoundForNonExistingUserId() throws Exception {
        ResponseEntity<String> response = get("/cal/" + UUID.randomUUID().toString(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void badRequestForInvalidUUID() throws Exception {
        ResponseEntity<String> response = get("/cal/3132", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void hasVCalendarString() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(user, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        ResponseEntity<String> response = get("/cal/" + user.getUid().toString(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("VCALENDAR");
    }

    @Test
    public void hasContentHeaders() throws Exception {
        UserAccount user = userRepository.save(Factory.userAccount());
        leaveRepository.save(Factory.leave(user, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14)));

        ResponseEntity<String> response = get("/cal/" + user.getUid().toString(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get("Content-Type").get(0)).isEqualTo("text/calendar");
        assertThat(response.getHeaders().get("Content-Disposition").get(0)).startsWith("attachment;filename=").endsWith(".ics");
    }

}
