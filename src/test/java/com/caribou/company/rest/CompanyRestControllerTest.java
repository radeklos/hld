package com.caribou.company.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.rest.dto.CompanyDto;
import com.caribou.company.service.parser.EmployeeCsvParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import rx.observers.TestSubscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class CompanyRestControllerTest extends IntegrationTests {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeCsvParser employeeCsvParser;

    @Autowired
    private DepartmentRepository departmentRepository;

    private UserAccount userAccount;

    private String userPassword;

    @Before
    public void setup() throws Exception {
        userAccount = Factory.userAccount();
        userPassword = userAccount.getPassword();
        userService.create(userAccount).subscribe(new TestSubscriber<>());
    }

    @Test
    public void whenUserRequestCompanyWhereHeDoesNotBelongToReturns404() throws Exception {
        Company company = Factory.company();
        companyRepository.save(company);

        ResponseEntity<CompanyDto> response = get(
                String.format("/v1/companies/%s", company.getUid()),
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getCompany() throws Exception {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Owner);
        companyRepository.save(company);

        ResponseEntity<CompanyDto> response = get(
                String.format("/v1/companies/%s", company.getUid()),
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        CompanyDto companyDto = response.getBody();
//        assertThat(companyDto.getUid()).isEqualTo(company.getUid());
        assertThat(companyDto.getName()).isEqualTo(company.getName());
        assertThat(companyDto.getDefaultDaysOff()).isEqualTo(company.getDefaultDaysOff());
    }

    @Test
    public void requestWithEmptyJsonRequestReturnsUnprocessableEntity() throws Exception {
        CompanyDto company = CompanyDto.newBuilder().build();
        ResponseEntity<CompanyDto> response = post(
                "/v1/companies",
                company,
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void createNewCompany() throws Exception {
        CompanyDto company = Factory.companyDto();
        ResponseEntity<CompanyDto> response = post(
                "/v1/companies",
                company,
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void updateCompany() throws Exception {
        Company company = Factory.company();
        companyRepository.save(company);
        CompanyDto companyDto = Factory.companyDto();
        ResponseEntity<CompanyDto> response = put(
                String.format("/v1/companies/%s", company.getUid()),
                companyDto,
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(companyDto.getName());
        assertThat(response.getBody().getAddress1()).isEqualTo(companyDto.getAddress1());
        assertThat(response.getBody().getCity()).isEqualTo(companyDto.getCity());
        assertThat(response.getBody().getDefaultDaysOff()).isEqualTo(companyDto.getDefaultDaysOff());
        assertThat(response.getBody().getRegNo()).isEqualTo(companyDto.getRegNo());
    }

    @Test
    public void updateNonExistingCompany() throws Exception {
        CompanyDto companyDto = Factory.companyDto();
        ResponseEntity<CompanyDto> response = put(
                "/v1/companies/0",
                companyDto,
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void nonExisting() throws Exception {
        ResponseEntity<CompanyDto> response = get(
                "/v1/companies/0",
                CompanyDto.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void companyHasLinkToDepartment() throws Exception {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Owner);
        companyRepository.save(company);

        ResponseEntity<HashMap> response = get(
                String.format("/v1/companies/%s", company.getUid()),
                HashMap.class,
                userAccount.getEmail(),
                userPassword
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrDefault("_links", null)).isNotNull();
    }

    @Test
    public void uploadCsvWithEmployeesAsViewerIsForbidden() throws IOException {
        Company company = Factory.company();
        companyRepository.save(company);
        companyRepository.addEmployee(company, userAccount, Role.Viewer);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(File.createTempFile("abc", ".csv")));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("X-Authorization", String.format("Bearer %s", getUserToken(userAccount.getEmail(), userPassword)));
        ResponseEntity result = testRestTemplate().exchange(
                path(String.format("/v1/companies/%s/employees", company.getUid())),
                HttpMethod.POST,
                new HttpEntity<>(parts, httpHeaders),
                String.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void onlyCompanyAdminAndEditorCanImportEmployees() throws Exception {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Editor);
        companyRepository.save(company);
        departmentRepository.save(Department.newBuilder().company(company).name("HR").daysOff(10).build());

        File myFoo = File.createTempFile("employees", ".csv");
        FileOutputStream fooStream = new FileOutputStream(myFoo, false);
        fooStream.write(employeeCsvParser.generateExample().getBytes());
        fooStream.close();

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(myFoo));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("X-Authorization", String.format("Bearer %s", getUserToken(userAccount.getEmail(), userPassword)));
        ResponseEntity result = testRestTemplate().exchange(
                path(String.format("/v1/companies/%s/employees", company.getUid())),
                HttpMethod.POST,
                new HttpEntity<>(parts, httpHeaders),
                Object.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    public void sendInvitationsToImportedUsers() throws Exception {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Editor);
        companyRepository.save(company);
        departmentRepository.save(Department.newBuilder().company(company).name("HR").daysOff(10).build());

        String file =
                "first name,last name,email,department,reaming holiday\n" +
                faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",HR,21\n" +
                        faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",HR,21\n";
        File myFoo = File.createTempFile("employees", ".csv");
        FileOutputStream fooStream = new FileOutputStream(myFoo, false);
        fooStream.write(file.getBytes());
        fooStream.close();

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(myFoo));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("X-Authorization", String.format("Bearer %s", getUserToken(userAccount.getEmail(), userPassword)));
        ResponseEntity<String> result = testRestTemplate().exchange(
                path(String.format("/v1/companies/%s/employees", company.getUid())),
                HttpMethod.POST,
                new HttpEntity<>(parts, httpHeaders),
                String.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(emailSender, times(2)).send(any(), eq(Locale.UK));
    }

    @Ignore("User import is not transactional")
    public void doNotSendEmyEmailWhenImportFails() throws Exception {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Editor);
        companyRepository.save(company);
        departmentRepository.save(Department.newBuilder().company(company).name("HR").daysOff(10).build());

        String file =
                "first name,last name,email,department,reaming holiday\n" +
                        faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",HR,21\n" +
                        faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",Foo,21\n";
        File myFoo = File.createTempFile("employees", ".csv");
        FileOutputStream fooStream = new FileOutputStream(myFoo, false);
        fooStream.write(file.getBytes());
        fooStream.close();

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(myFoo));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("X-Authorization", String.format("Bearer %s", getUserToken(userAccount.getEmail(), userPassword)));
        ResponseEntity result = testRestTemplate().exchange(
                path(String.format("/v1/companies/%s/employees", company.getUid())),
                HttpMethod.POST,
                new HttpEntity<>(parts, httpHeaders),
                Object.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(emailSender, times(0)).send(any(), eq(Locale.UK));
    }

    @Test
    public void createsUnknownDepartment() throws IOException {
        Company company = Factory.company();
        company.addEmployee(userAccount, Role.Editor);
        companyRepository.save(company);

        File myFoo = File.createTempFile("employees", ".csv");
        FileOutputStream fooStream = new FileOutputStream(myFoo, false);

        String file =
                "first name,last name,email,department,reaming holiday\n" +
                        faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",HR,21\n" +
                        faker.name().firstName() + "," + faker.name().lastName() + "," + faker.internet().emailAddress() + ",\"" + faker.commerce().department() + "\",21\n";
        fooStream.write(file.getBytes());
        fooStream.close();

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new FileSystemResource(myFoo));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("X-Authorization", String.format("Bearer %s", getUserToken(userAccount.getEmail(), userPassword)));
        ResponseEntity result = testRestTemplate().exchange(
                path(String.format("/v1/companies/%s/employees", company.getUid())),
                HttpMethod.POST,
                new HttpEntity<>(parts, httpHeaders),
                Object.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    public void downloadExampleEmployeesCsv() throws Exception {
        ResponseEntity<byte[]> response = get(
                "/v1/companies/examples/employees",
                byte[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(new MediaType("text", "csv"));
        assertThat(response.getBody().length).isNotZero();
    }

}
