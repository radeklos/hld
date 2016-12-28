package com.caribou.company.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.service.UserService;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.dto.CompanyDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rx.observers.TestSubscriber;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class CompanyRestControllerTest extends IntegrationTests {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

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

}
