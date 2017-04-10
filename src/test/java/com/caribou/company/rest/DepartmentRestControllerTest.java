package com.caribou.company.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.rest.dto.DepartmentDto;
import com.caribou.company.rest.dto.EmployeeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.observers.TestObserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class DepartmentRestControllerTest extends IntegrationTests {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserService userService;

    private UserAccount userAccount;

    private Company company;

    private String userPassword;

    @Before
    public void before() throws Exception {
        userAccount = Factory.userAccount();
        userPassword = userAccount.getPassword();
        userService.create(userAccount).subscribe(new TestObserver<>());

        company = Factory.company();
        companyRepository.save(company);

        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);
        company = companyRepository.findOne(company.getUid());
    }

    @Test
    public void nonExistingCompanyReturn404() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/0/departments/%s", department.getUid());
        ResponseEntity<DepartmentDto> response = get(url, DepartmentDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDepartment() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = get(url, DepartmentDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto body = response.getBody();
        assertThat(body.getName()).isEqualTo(department.getName());
        assertThat(body.getDaysOff()).isEqualTo(new Integer(10));
    }

    @Test
    public void departmentHasLinkToEmployee() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<HashMap> response = get(url, HashMap.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrDefault("_links", null)).isNotNull();
    }

    @Test
    public void getList() throws Exception {
        Department hr = Factory.department(company);
        Department account = Factory.department(company);
        departmentRepository.save(Arrays.asList(hr, account));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = get(url, DepartmentDto[].class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto[] departments = response.getBody();
        assertThat(departments.length).isEqualTo(2);
    }

    @Test
    public void getListOfDepartmentsInEmployeesCompanyOnly() throws Exception {
        Department hr = Factory.department(company);
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        Department account = Factory.department(anotherCompany);
        departmentRepository.save(Arrays.asList(hr, account));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto[]> response = get(url, DepartmentDto[].class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto[] departments = response.getBody();
        assertThat(departments.length).isEqualTo(1);
        assertThat(departments[0].getName()).isEqualTo(hr.getName());
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);
        DepartmentDto departmentDto = Factory.departmentDto();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = put(
                url,
                departmentDto,
                DepartmentDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentDto body = response.getBody();
        assertThat(body.getName()).isEqualTo(departmentDto.getName());
        assertThat(body.getDaysOff()).isEqualTo(departmentDto.getDaysOff());

        department = departmentRepository.findOne(department.getUid());
        assertThat(department.getName()).isEqualTo(departmentDto.getName());
        assertThat(department.getDaysOff()).isEqualTo(departmentDto.getDaysOff());
    }

    @Test
    public void updateNonExistingDepartmentReturns404() throws Exception {
        DepartmentDto departmentDto = Factory.departmentDto();
        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), 0);
        ResponseEntity<DepartmentDto> response = put(url, departmentDto, DepartmentDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDepartmentAsGuestReturnUnauthorized() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = get(url, DepartmentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void createDepartmentAsGuestReturnUnauthorized() throws JsonProcessingException {
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = post(url, departmentDto, DepartmentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateDepartmentAsGuestReturnUnauthorized() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);
        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("new name").daysOff(12).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentDto> response = put(url, departmentDto, DepartmentDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getListOfDepartmentsForUnemployedReturns404() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<HashMap> response = get(url, HashMap.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createNewDepartmentAsUnemployedReturns404() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<DepartmentDto> response = post(
                url,
                departmentDto,
                DepartmentDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createNewDepartmentAsViewerReturns401() throws Exception {
        UserAccount viewer = Factory.userAccount();
        String viewPassword = viewer.getPassword();
        userService.create(viewer).subscribe(new TestObserver<>());
        companyRepository.addEmployee(company, viewer, Role.Viewer);

        DepartmentDto departmentDto = Factory.departmentDto();
        int size = Lists.from(departmentRepository.findAll().iterator()).size();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = post(
                url,
                departmentDto,
                DepartmentDto.class,
                viewer.getEmail(),
                viewPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        ArrayList<Department> departments = new ArrayList<>();
        departmentRepository.findAll().forEach(departments::add);

        assertThat(departments.size()).isEqualTo(size);
    }

    @Test
    public void createNewDepartmentAsAdmin() throws Exception {
        UserAccount admin = Factory.userAccount();
        String adminPassword = admin.getPassword();
        userService.create(admin).subscribe(new TestObserver<>());
        companyRepository.addEmployee(company, admin, Role.Admin);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = post(
                url,
                departmentDto,
                DepartmentDto.class,
                admin.getEmail(),
                adminPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Department department = departmentRepository.findOne(response.getBody().getUid());
        assertThat(department.getCompany().getUid()).as("Department isn't saved into company").isEqualTo(company.getUid());
    }

    @Test
    public void createNewDepartmentAsEditor() throws Exception {
        UserAccount editor = Factory.userAccount();
        String editorPassword = editor.getPassword();
        userService.create(editor).subscribe(new TestObserver<>());
        companyRepository.addEmployee(company, editor, Role.Editor);

        DepartmentDto departmentDto = DepartmentDto.newBuilder().name("department").daysOff(10).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentDto> response = post(
                url,
                departmentDto,
                DepartmentDto.class,
                editor.getEmail(),
                editorPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Department department = departmentRepository.findOne(response.getBody().getUid());
        assertThat(department.getCompany().getUid()).isEqualTo(company.getUid());
    }

    @Test
    public void getDepartmentEmployees() throws Exception {
        UserAccount anotherUserAccount = Factory.userAccount();

        userRepository.save(anotherUserAccount);
        companyRepository.save(company);
        companyRepository.addEmployee(company, anotherUserAccount, Role.Viewer);

        Department department = Factory.department(company);
        Department anotherDepartment = Factory.department(company);
        departmentRepository.save(Arrays.asList(department, anotherDepartment));

        departmentRepository.addEmployee(anotherDepartment, anotherUserAccount, BigDecimal.TEN, Role.Editor);
        departmentRepository.addEmployee(department, userAccount, BigDecimal.TEN, Role.Admin);

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
        ResponseEntity<EmployeeDto[]> response = get(
                url,
                EmployeeDto[].class,
                userAccount.getEmail(),
                userPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(1);
        EmployeeDto employee = response.getBody()[0];
        assertThat(employee.getEmail()).isEqualTo(userAccount.getEmail());
        assertThat(employee.getFirstName()).isEqualTo(userAccount.getFirstName());
        assertThat(employee.getLastName()).isEqualTo(userAccount.getLastName());
        assertThat(employee.getRole()).isEqualTo(Role.Admin);
        assertThat(employee.getUid()).isEqualTo(userAccount.getUid());
    }

}
