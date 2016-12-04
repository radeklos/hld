package com.caribou.company.repository;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;


public class CompanyRepositoryTest extends IntegrationTests {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    private Company company;

    private UserAccount userAccount;

    @Before
    public void setUp() throws Exception {
        company = Factory.company();
        companyRepository.save(company);

        userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        company = companyRepository.findOne(company.getUid());
    }

    @Test
    public void findEmployeeByEmailForUid() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), company.getUid());
        assertThat(result).isNotNull();
    }

    @Test
    public void findEmployeeByEmailForUidAnotherCompany() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        Company result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), anotherCompany.getUid());
        assertThat(result).isNull();
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingEmail() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid("non.existing@email.com", company.getUid());
        assertThat(result).isNull();
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingCompany() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), 0L);
        assertThat(result).isNull();
    }

    @Test
    public void onlyOneEmployeeCanBeInSameCompany() {
        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        Company refreshed = companyRepository.findOne(company.getUid());
        assertThat(refreshed.getEmployees()).hasSize(1);
    }

    @Test
    public void changeEmployeeRoleInCompany() {
        Role role = company.getEmployees().iterator().next().getRole();
        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);

        Company refreshed = companyRepository.findOne(company.getUid());

        assertNotEquals(Role.Admin, role);
        assertThat(refreshed.getEmployees().iterator().next().getRole()).isEqualTo(Role.Admin);
    }

    @Test
    public void changeEmployeeRoleInCompanyDoNotChangeOtherUsers() {
        UserAccount anotherUserAccount = Factory.userAccount();
        userRepository.save(anotherUserAccount);

        company.addEmployee(anotherUserAccount, Role.Viewer);
        companyRepository.save(company);
        company = companyRepository.findOne(company.getUid());

        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);
        company = companyRepository.findOne(company.getUid());

        Company refreshed = companyRepository.findOne(company.getUid());

        assertThat(refreshed.getEmployees()).hasSize(2);

        CompanyEmployee anotherUserCompany = userRepository.findOne(anotherUserAccount.getUid()).getCompanies().iterator().next();
        assertThat(anotherUserCompany.getRole()).isEqualTo(Role.Viewer);

        CompanyEmployee defaultUserCompany = userRepository.findOne(userAccount.getUid()).getCompanies().iterator().next();
        assertThat(defaultUserCompany.getRole()).isEqualTo(Role.Admin);
    }

}
