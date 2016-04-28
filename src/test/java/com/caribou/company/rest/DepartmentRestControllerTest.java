package com.caribou.company.rest;

import com.caribou.Factory;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.internal.util.Lists;
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

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {WebApplication.class})
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
public class DepartmentRestControllerTest {

    private static TestRestTemplate restAuthenticated;

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
        userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        company = Factory.company();
        companyRepository.save(company);

        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);
        company = companyRepository.findOne(company.getUid());

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restGuest.setRequestFactory(requestFactory);

        restAuthenticated = new TestRestTemplate(userAccount.getEmail(), userAccount.getPassword());
    }

    @Test
    public void nonExistingCompanyReturn404() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/0/departments/%s", department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getDepartment() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.getForEntity(path(url), DepartmentDto.class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto body = response.getBody();
        assertEquals(department.getName(), body.getName());
        assertEquals(new Integer(10), body.getDaysOff());
    }

    @Test
    public void getList() throws Exception {
        Department hr = Factory.department(company);
        Department account = Factory.department(company);
        departmentRepository.save(Arrays.asList(hr, account));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = restAuthenticated.getForEntity(path(url), DepartmentDto[].class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto[] departments = response.getBody();
        assertEquals(2, departments.length);
    }

    @Test
    public void getListOfDepartmentsInEmploeeysCompanyOnly() {
        Department hr = Factory.department(company);
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        Department account = Factory.department(anotherCompany);
        departmentRepository.save(Arrays.asList(hr, account));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = restAuthenticated.getForEntity(path(url), DepartmentDto[].class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto[] departments = response.getBody();
        assertEquals(1, departments.length);
        assertEquals(hr.getName(), departments[0].getName());
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);
        DepartmentDto departmentDto = Factory.departmentDto();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.POST, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        DepartmentDto body = response.getBody();
        assertEquals(departmentDto.getName(), body.getName());
        assertEquals(departmentDto.getDaysOff(), body.getDaysOff());

        department = departmentRepository.findOne(department.getUid());
        assertEquals(departmentDto.getName(), department.getName());
        assertEquals(departmentDto.getDaysOff(), department.getDaysOff());
    }

    @Test
    public void updateNonExistingDepartmentReturns404() throws Exception {
        DepartmentDto departmentDto = Factory.departmentDto();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), 0);
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.POST, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getDepartmentAsGuestReturnUnauthorized() {
        Department department = Factory.department(company);
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
        Department department = Factory.department(company);
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
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(
                path(url),
                HttpMethod.PUT,
                new HttpEntity<>(departmentDto, new HttpHeaders()),
                DepartmentDto.class
        );

        assertEquals(url, HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void createNewDepartmentAsViewerReturns401() {
        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        DepartmentDto departmentDto = Factory.departmentDto();
        int size = Lists.from(departmentRepository.findAll().iterator()).size();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.FORBIDDEN, response.getStatusCode());
        ArrayList<Department> departments = new ArrayList<Department>();
        departmentRepository.findAll().forEach(departments::add);

        assertEquals(size, departments.size());
    }

    @Test
    public void createNewDepartmentAsAdmin() throws Exception {
        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(path(url), HttpMethod.PUT, new HttpEntity<>(departmentDto, new HttpHeaders()), DepartmentDto.class);

        assertEquals(url, HttpStatus.CREATED, response.getStatusCode());

        Department department = departmentRepository.findOne(response.getBody().getUid());
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

        Department department = departmentRepository.findOne(response.getBody().getUid());
        assertEquals("Department isn't saved into company", company.getUid(), department.getCompany().getUid());
    }

    @Ignore
    public void getDepartmentEmployees() {
        UserAccount anotherUserAccount = Factory.userAccount();

        userRepository.save(anotherUserAccount);
        company.addEmployee(anotherUserAccount, Role.Viewer);
        companyRepository.save(company);

        Department department = Factory.department(company);
        Department anotherDepartment = Factory.department(company);
        departmentRepository.save(Arrays.asList(department, anotherDepartment));

        anotherDepartment.addEmployee(anotherUserAccount, Role.Editor);
        department.addEmployee(userAccount, Role.Admin);
        departmentRepository.save(Arrays.asList(department, anotherDepartment));

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = restAuthenticated.exchange(
                path(url),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                DepartmentDto.class
        );
        assertEquals(url, HttpStatus.OK, response.getStatusCode());

        assertEquals(1, response.getBody());
    }

    private String path(String context) {
        return String.format("http://localhost:%s%s", port, context);
    }

}
