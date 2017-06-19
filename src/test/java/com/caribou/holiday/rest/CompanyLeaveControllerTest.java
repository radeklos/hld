package com.caribou.holiday.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.service.CompanyService;
import com.caribou.holiday.domain.LeaveType;
import com.caribou.holiday.repository.LeaveTypeRepository;
import com.caribou.holiday.rest.dto.ListDto;
import com.caribou.holiday.service.LeaveService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.observers.TestSubscriber;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CompanyLeaveControllerTest extends IntegrationTests {

    Company company;

    UserAccount userAccount;

    LeaveType leaveType;

    private String password;

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
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        userAccount = Factory.userAccount();
        password = userAccount.getPassword();

        company = companyRepository.save(Factory.company());
        userService.create(userAccount).subscribe(new TestSubscriber<>());
        leaveType = leaveTypeRepository.save(LeaveType.newBuilder().company(company).name("Holiday").build());
    }

    @Test
    public void getList() throws Exception {
        UserAccount emp1 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp1, Role.Viewer);
        leaveService.create(Factory.leave(emp1, leaveType, LocalDate.of(2017, 4, 25), LocalDate.of(2017, 5, 14))).subscribe(new TestSubscriber<>());

        UserAccount emp2 = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, emp2, Role.Viewer);
        leaveService.create(Factory.leave(emp2, leaveType, LocalDate.of(2017, 5, 25), LocalDate.of(2017, 6, 14))).subscribe(new TestSubscriber<>());
        leaveService.create(Factory.leave(emp2, leaveType, LocalDate.of(2017, 6, 1), LocalDate.of(2017, 6, 14))).subscribe(new TestSubscriber<>());

        String url = String.format("/v1/company/%s/leaves?from=2017-05-01&to=2017-05-31", company.getUid());
        ResponseEntity<ListDto> response = get(
                url,
                ListDto.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ListDto body = response.getBody();
        assertThat(body.getTotal()).isEqualTo(2);

        LinkedHashMap employeeLeavesDto = (LinkedHashMap) body.getItems().get(0);
        LinkedHashMap employeeDto = (LinkedHashMap) employeeLeavesDto.get("employee");
        assertThat(employeeDto.get("email")).isEqualTo(emp1.getEmail());

        List<LinkedHashMap> leavesDto = (List<LinkedHashMap>) employeeLeavesDto.get("leaves");
        assertThat(leavesDto.get(0).get("from")).isEqualTo("2017-04-25");
        assertThat(leavesDto.get(0).get("to")).isEqualTo("2017-05-14");
    }

    @Test
    public void getListWithoutDateRange() throws Exception {
        String url = String.format("/v1/company/%s/leaves", company.getUid());
        ResponseEntity<String> response = get(
                url,
                String.class,
                userAccount.getEmail(),
                password
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
