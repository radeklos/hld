package com.caribou.company.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.rest.dto.DepartmentReadDto;
import com.caribou.company.rest.dto.DepartmentWriteDto;
import com.caribou.company.rest.dto.EmployeeDto;
import com.caribou.holiday.rest.dto.ListDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.internal.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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
    private Department department;

    @Before
    public void before() throws Exception {
        userAccount = Factory.userAccount();
        userPassword = userAccount.getPassword();
        userService.create(userAccount);

        company = Factory.company();
        companyRepository.save(company);

        department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        companyRepository.addEmployee(company, department, userAccount, Role.Admin);
        company = companyRepository.findOne(company.getUid());
    }

    @Test
    public void nonExistingCompanyReturn404() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/0/departments/%s", department.getUid());
        ResponseEntity<DepartmentWriteDto> response = get(url, DepartmentWriteDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDepartment() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentReadDto> response = get(url, DepartmentReadDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentReadDto body = response.getBody();
        assertThat(body.getName()).isEqualTo(department.getName());
        assertThat(body.getDaysOff()).isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(body.getBoss().getUid()).isEqualTo(userAccount.getUid().toString());
        assertThat(body.getBoss().getLabel()).isEqualTo(userAccount.getFullName());
    }

    @Test
    public void departmentHasLinkToEmployee() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<HashMap> response = get(url, HashMap.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrDefault("_links", null)).isNotNull();
    }

    @Test
    public void getList() throws Exception {
        Department hr = Factory.department(company, userAccount);
        Department account = Factory.department(company, userAccount);
        departmentRepository.save(Arrays.asList(hr, account));

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<ListDto> response = get(url, ListDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ListDto departments = response.getBody();
        assertThat(departments.getTotal()).isEqualTo(3);
    }

    @Test
    public void getListOfDepartmentsInEmployeesCompanyOnly() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        Department account = Factory.department(anotherCompany, userAccount);
        departmentRepository.save(account);

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<ListDto> response = get(url, ListDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ListDto departments = response.getBody();
        assertThat(departments.getTotal()).isEqualTo(1);
        assertThat(((HashMap) departments.getItems().get(0)).get("name")).isEqualTo(department.getName());
    }

    @Test
    public void updateDepartment() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);
        DepartmentWriteDto departmentDto = Factory.departmentDto(userAccount.getUid().toString());

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentWriteDto> response = put(
                url,
                departmentDto,
                DepartmentWriteDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DepartmentWriteDto body = response.getBody();
        assertThat(body.getName()).isEqualTo(departmentDto.getName());
        assertThat(body.getDaysOff()).isEqualTo(departmentDto.getDaysOff());

        department = departmentRepository.findOne(department.getUid());
        assertThat(department.getName()).isEqualTo(departmentDto.getName());
        assertThat(department.getDaysOff()).isEqualByComparingTo(departmentDto.getDaysOff());
    }

    @Test
    public void updateNonExistingDepartmentReturns404() throws Exception {
        DepartmentWriteDto departmentDto = Factory.departmentDto(userAccount.getUid().toString());
        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), 0);
        ResponseEntity<DepartmentWriteDto> response = put(url, departmentDto, DepartmentWriteDto.class, userAccount.getEmail(), userPassword);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getDepartmentAsGuestReturnUnauthorized() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentWriteDto> response = get(url, DepartmentWriteDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void createDepartmentAsGuestReturnUnauthorized() throws JsonProcessingException {
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder().name("department").daysOff(BigDecimal.TEN).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentWriteDto> response = post(url, departmentDto, DepartmentWriteDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateDepartmentAsGuestReturnUnauthorized() throws Exception {
        Department department = Factory.department(company, userAccount);
        departmentRepository.save(department);
        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder().name("new name").daysOff(BigDecimal.valueOf(12)).build();

        String url = String.format("/v1/companies/%s/departments/%s", company.getUid(), department.getUid());
        ResponseEntity<DepartmentWriteDto> response = put(url, departmentDto, DepartmentWriteDto.class);

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

        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .boss(userAccount.getUid().toString())
                .name("department").daysOff(BigDecimal.TEN).build();

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<DepartmentWriteDto> response = post(
                url,
                departmentDto,
                DepartmentWriteDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createNewDepartmentWithoutBossReturns422() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder().name("department").daysOff(BigDecimal.TEN).build();

        String url = String.format("/v1/companies/%s/departments", anotherCompany.getUid());
        ResponseEntity<DepartmentWriteDto> response = post(
                url,
                departmentDto,
                DepartmentWriteDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void createNewDepartmentAsViewerReturns401() throws Exception {
        UserAccount viewer = Factory.userAccount();
        String viewPassword = viewer.getPassword();
        userService.create(viewer);
        companyRepository.addEmployee(company, viewer, Role.Viewer);

        DepartmentWriteDto departmentDto = Factory.departmentDto(viewer.getUid().toString());
        int size = Lists.from(departmentRepository.findAll().iterator()).size();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentWriteDto> response = post(
                url,
                departmentDto,
                DepartmentWriteDto.class,
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
        userService.create(admin);
        companyRepository.addEmployee(company, admin, Role.Admin);

        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .boss(admin.getUid().toString())
                .name("department").daysOff(BigDecimal.TEN).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentReadDto> response = post(
                url,
                departmentDto,
                DepartmentReadDto.class,
                admin.getEmail(),
                adminPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Department department = departmentRepository.findOne(UUID.fromString(response.getBody().getUid()));
        assertThat(department.getCompany().getUid()).isEqualTo(company.getUid());
        assertThat(department.getBoss()).isEqualTo(admin);
        assertThat(response.getBody().getBoss().getUid()).isEqualTo(admin.getUid().toString());
        assertThat(response.getBody().getBoss().getLabel()).isEqualTo(admin.getFullName());
    }

    @Test
    public void createNewDepartmentAsEditor() throws Exception {
        UserAccount editor = Factory.userAccount();
        String editorPassword = editor.getPassword();
        userService.create(editor);
        companyRepository.addEmployee(company, editor, Role.Editor);

        DepartmentWriteDto departmentDto = DepartmentWriteDto.builder()
                .boss(userAccount.getUid().toString())
                .name("department").daysOff(BigDecimal.TEN).build();

        String url = String.format("/v1/companies/%s/departments", company.getUid());
        ResponseEntity<DepartmentReadDto> response = post(
                url,
                departmentDto,
                DepartmentReadDto.class,
                editor.getEmail(),
                editorPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Department department = departmentRepository.findOne(UUID.fromString(response.getBody().getUid()));
        assertThat(department.getCompany().getUid()).isEqualTo(company.getUid());
    }

    @Test
    public void getDepartmentEmployees() throws Exception {
        UserAccount anotherUserAccount = Factory.userAccount();

        Department anotherDepartment = Factory.department(company, userAccount);
        departmentRepository.save(anotherDepartment);

        userRepository.save(anotherUserAccount);
        companyRepository.addEmployee(company, anotherDepartment, anotherUserAccount, Role.Viewer);

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
        ResponseEntity<ListDto> response = get(
                url,
                ListDto.class,
                userAccount.getEmail(),
                userPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTotal()).isEqualTo(1);
        HashMap employee = (HashMap) response.getBody().getItems().get(0);
        assertThat(employee.get("email")).isEqualTo(userAccount.getEmail());
        assertThat(employee.get("firstName")).isEqualTo(userAccount.getFirstName());
        assertThat(employee.get("lastName")).isEqualTo(userAccount.getLastName());
        assertThat(employee.get("role")).isEqualTo(Role.Admin.toString());
        assertThat(employee.get("uid")).isEqualTo(userAccount.getUid().toString());
    }

    @Test
    public void getListOfEmployeesFromAnotherCompany() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        Department anotherDepartment = Factory.department(anotherCompany, userAccount);
        departmentRepository.save(anotherDepartment);

        String url = String.format("/v1/companies/%s/departments/%s/employees", anotherCompany.getUid(), department.getUid());
        ResponseEntity<ListDto> response = get(
                url,
                ListDto.class,
                userAccount.getEmail(),
                userPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getListOfEmployeesFromDepartmentWhichIsNotInCompany() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);
        Department anotherDepartment = Factory.department(anotherCompany, userAccount);
        departmentRepository.save(anotherDepartment);

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), anotherDepartment.getUid());
        ResponseEntity<ListDto> response = get(
                url,
                ListDto.class,
                userAccount.getEmail(),
                userPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void createNewEmployee() throws Exception {
        EmployeeDto employeeDto = EmployeeDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .startedAt(LocalDate.now()).build();

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
        ResponseEntity<EmployeeDto> response = post(
                url,
                employeeDto,
                EmployeeDto.class,
                userAccount.getEmail(),
                userPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Optional<CompanyEmployee> createdUser = companyRepository.findEmployeeByEmail(employeeDto.getEmail());
        assertThat(createdUser).isPresent();
        assertThat(createdUser.get().getCompany().getUid()).isEqualTo(company.getUid());
        assertThat(createdUser.get().getDepartment().getUid()).isEqualTo(department.getUid());
    }

    @Test
    public void viewerCannotCreateNewUser() throws Exception {
        UserAccount viewer = Factory.userAccount();
        String viewerPassword = viewer.getPassword();
        userService.create(viewer);
        companyRepository.addEmployee(department, viewer, Role.Viewer, BigDecimal.TEN);

        EmployeeDto employeeDto = EmployeeDto.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .startedAt(LocalDate.now()).build();

        String url = String.format("/v1/companies/%s/departments/%s/employees", company.getUid(), department.getUid());
        ResponseEntity<EmployeeDto> response = post(
                url,
                employeeDto,
                EmployeeDto.class,
                viewer.getEmail(),
                viewerPassword
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
