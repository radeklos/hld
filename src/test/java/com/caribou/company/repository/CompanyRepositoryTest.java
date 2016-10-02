package com.caribou.company.repository;

import com.caribou.Factory;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class CompanyRepositoryTest {

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
        assertNotNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidAnotherCompany() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        Company result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), anotherCompany.getUid());
        assertNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingEmail() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid("non.existing@email.com", company.getUid());
        assertNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingCompany() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), 0L);
        assertNull(result);
    }

    @Test
    public void onlyOneEmployeeCanBeInSameCompany() {
        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        Company refreshed = companyRepository.findOne(company.getUid());
        assertEquals(1, refreshed.getEmployees().size());
    }

    @Test
    public void changeEmployeeRoleInCompany() {
        Role role = company.getEmployees().iterator().next().getRole();
        company.addEmployee(userAccount, Role.Admin);
        companyRepository.save(company);

        Company refreshed = companyRepository.findOne(company.getUid());

        assertNotEquals(Role.Admin, role);
        assertEquals(Role.Admin, refreshed.getEmployees().iterator().next().getRole());
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

        assertEquals(2, refreshed.getEmployees().size());

        CompanyEmployee anotherUserCompany = userRepository.findOne(anotherUserAccount.getUid()).getCompanies().iterator().next();
        assertEquals(Role.Viewer, anotherUserCompany.getRole());

        CompanyEmployee defaultUserCompany = userRepository.findOne(userAccount.getUid()).getCompanies().iterator().next();
        assertEquals(Role.Admin, defaultUserCompany.getRole());
    }

}
