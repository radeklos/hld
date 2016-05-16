package com.caribou.company.rest;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.rest.dto.DepartmentDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.TestCase.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class DepartmentRestControllerTest {

    private static TestRestTemplate restAuthenticated = new TestRestTemplate("john.doe@email.com", "abcabc");

    private static TestRestTemplate restGuest = new TestRestTemplate();

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Value("${local.server.port}")
    private int port = 0;
    private UserAccount userAccount;
    private Company company;

    @Before
    public void setup() throws Exception {
        userRepository.deleteAll();

        userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();

        company = Company.newBuilder()
                .name("company")
                .defaultDaysOf(10)
                .build();

        userRepository.save(userAccount);
        companyRepository.save(company);


        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restGuest.setRequestFactory(requestFactory);
    }

    @After
    public void tearDown() throws Exception {
        departmentRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void nonExistingCompanyReturn404() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOf(10)
                .build();
        departmentRepository.save(department);

        String url = String.format("/v1/companies/0/departments/%s", department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createNewDepartment() throws Exception {
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        Department department = departmentRepository.findAll().iterator().next();
        assertEquals("Department isn't saved into company", company.getUid(), department.getCompany().getUid());
    }

    @Test
    public void getDepartment() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOf(10)
                .build();
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto body = response.getBody();
        assertEquals("department", body.getName());
        assertEquals(new Integer(10), body.getDaysOff());
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOf(10)
                .build();
        departmentRepository.save(department);
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("new name").daysOff(12).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.POST, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto body = response.getBody();
        assertEquals("new name", body.getName());
        assertEquals(new Integer(12), body.getDaysOff());

        department = departmentRepository.findOne(department.getUid());
        assertEquals("new name", department.getName());
        assertEquals(new Integer(12), department.getDaysOff());
    }

    @Test
    public void updateNonExistingDepartmentReturns404() throws Exception {
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("new name").daysOff(12).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), 0);
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.POST, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getDepartmentAsGuestReturnUnauthorized() {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOf(10)
                .build();
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restGuest.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void createDepartmentAsGuestReturnUnauthorized() {
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restGuest.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void updateDepartmentAsGuestReturnUnauthorized() {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOf(10)
                .build();
        departmentRepository.save(department);
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("new name").daysOff(12).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restGuest.postForEntity(path(url), departmentDto, DepartmentDto.class);

        assertEquals(url, HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
