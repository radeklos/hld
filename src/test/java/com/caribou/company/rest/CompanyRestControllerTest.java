package com.caribou.company.rest;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.rest.dto.CompanyDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;


public class CompanyRestControllerTest extends IntegrationTests {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    UserAccount userAccount;

    @Before
    public void setup() throws Exception {
        userAccount = Factory.userAccount();
        userRepository.save(userAccount);
    }

    @Test
    public void whenUserRequestCompanyWhereHeDoesNotBelongToReturns404() throws Exception {
        Company company = Factory.company();
        companyRepository.save(company);

        ResponseEntity<CompanyDto> response = get(
                String.format("/v1/companies/%s", company.getUid()),
                CompanyDto.class,
                userAccount.getEmail(),
                userAccount.getPassword()
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
                userAccount.getPassword()
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
                userAccount.getPassword()
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
                userAccount.getPassword()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void updateCompany() throws Exception {
        Company company = Factory.company();
        companyRepository.save(company);
        CompanyDto companyDto = CompanyDto.newBuilder()
                .name("company name")
                .defaultDaysOf(10)
                .build();
        ResponseEntity<CompanyDto> response = put(
                String.format("/v1/companies/%s", company.getUid()),
                companyDto,
                CompanyDto.class,
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void updateNonExistingCompany() throws Exception {
        CompanyDto companyDto = Factory.companyDto();
        ResponseEntity<CompanyDto> response = put(
                "/v1/companies/0",
                companyDto,
                CompanyDto.class,
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void nonExisting() throws Exception {
        ResponseEntity<CompanyDto> response = get(
                "/v1/companies/0",
                CompanyDto.class,
                userAccount.getEmail(),
                userAccount.getPassword()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
