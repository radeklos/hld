package com.caribou.company.rest;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.rest.dto.DepartmentDto;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
    public void before() throws Exception {
        userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        company = Company.newBuilder()
                .name("company")
                .defaultDaysOff(10)
                .build();
        companyRepository.save(company);

        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);
        company = companyRepository.findOne(company.getUid());

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restGuest.setRequestFactory(requestFactory);
    }

    @Test
    public void nonExistingCompanyReturn404() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOff(10)
                .build();
        departmentRepository.save(department);

        String url = String.format("/v1/companies/0/departments/%s", department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getDepartment() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOff(10)
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
    public void getList() throws Exception {
        Department hr = Department.newBuilder()
                .name("hr")
                .company(company)
                .daysOff(10)
                .build();
        Department account = Department.newBuilder()
                .name("account")
                .company(company)
                .daysOff(10)
                .build();
        departmentRepository.save(Arrays.asList(new Department[]{hr, account}));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = restAuthenticated.getForEntity(path(url), DepartmentDto[].class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto[] departments = response.getBody();
        assertEquals(2, departments.length);
    }

    @Test
    public void getListOfDepartmentsInEmploeeysCompanyOnly() {
        Department hr = Department.newBuilder()
                .name("hr")
                .company(company)
                .daysOff(10)
                .build();
        Company anotherCompany = Company.newBuilder().name("another company").defaultDaysOff(10).build();
        companyRepository.save(anotherCompany);
        Department account = Department.newBuilder()
                .name("account")
                .company(anotherCompany)
                .daysOff(10)
                .build();
        departmentRepository.save(Arrays.asList(new Department[]{hr, account}));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = restAuthenticated.getForEntity(path(url), DepartmentDto[].class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto[] departments = response.getBody();
        assertEquals(1, departments.length);
        assertEquals("hr", departments[0].getName());
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .company(company)
                .daysOff(10)
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
                .daysOff(10)
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
                .daysOff(10)
                .build();
        departmentRepository.save(department);
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("new name").daysOff(12).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restGuest.postForEntity(path(url), departmentDto, DepartmentDto.class);

        assertEquals(url, HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void getListOfDepartmentsForUnemployedReturns404() {
        Company anotherCompany = Company.newBuilder().name("another company").defaultDaysOff(10).build();
        companyRepository.save(anotherCompany);

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<Object> response = restAuthenticated.getForEntity(path(url), Object.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createNewDepartmentAsUnemployedReturns404() {
        Company anotherCompany = Company.newBuilder().name("another company").defaultDaysOff(10).build();
        companyRepository.save(anotherCompany);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createNewDepartmentAsViewerReturns401() {
        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.FORBIDDEN, response.getStatusCode());
        ArrayList<Department> departments = new ArrayList<Department>();
        departmentRepository.findAll().forEach(departments::add);

        assertEquals(0, departments.size());
    }

    @Test
    public void createNewDepartmentAsAdmin() throws Exception {
        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.CREATED, response.getStatusCode());

        Department department = departmentRepository.findAll().iterator().next();
        assertEquals("Department isn't saved into company", company.getUid(), department.getCompany().getUid());
    }

    @Test
    public void createNewDepartmentAsEditor() throws Exception {
        company.addEmployee(userAccount, Role.Editor);
        companyRepository.save(company);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.CREATED, response.getStatusCode());

        Department department = departmentRepository.findAll().iterator().next();
        assertEquals("Department isn't saved into company", company.getUid(), department.getCompany().getUid());
    }

//    @Test
//    public void getDepartmentEmployee() {
//        Department department = Department.newBuilder()
//                .name("department")
//                .company(company)
//                .daysOff(10)
//                .build();
//        departmentRepository.save(department);
//
//        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
//        ResponseEntity<EmployeeDto[]> response = restAuthenticated.getForEntity(path(url), EmployeeDto[].class);
//        assertEquals(url, HttpStatus.OK, response.getStatusCode());
//    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
