package com.caribou.company.repository;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static junit.framework.TestCase.assertNotNull;
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

    @Before
    public void setUp() throws Exception {
        company = Company.newBuilder().name("company").defaultDaysOff(10).build();
        companyRepository.save(company);

        UserAccount userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);
    }

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void findEmployeeByEmailForUid() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid("john.doe@email.com", company.getUid());
        assertNotNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidAnotherCompany() throws Exception {
        Company anotherCompany = Company.newBuilder().name("company").defaultDaysOff(10).build();
        companyRepository.save(anotherCompany);

        Company result = companyRepository.findEmployeeByEmailForUid("john.doe@email.com", anotherCompany.getUid());
        assertNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingEmail() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid("non.existing@email.com", company.getUid());
        assertNull(result);
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingCompany() throws Exception {
        Company result = companyRepository.findEmployeeByEmailForUid("john.doe@email.com", 0L);
        assertNull(result);
    }
}
