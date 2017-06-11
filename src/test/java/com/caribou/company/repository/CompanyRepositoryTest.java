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
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


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
        companyRepository.addEmployee(company, userAccount, Role.Admin);

        company = companyRepository.findOne(company.getUid());
    }

    @Test
    public void findEmployeeByEmailForUid() throws Exception {
        Optional<CompanyEmployee> result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), company.getUid());
        assertThat(result).isPresent();
    }

    @Test
    public void findEmployeeByEmailForUidAnotherCompany() throws Exception {
        Company anotherCompany = Factory.company();
        companyRepository.save(anotherCompany);

        Optional<CompanyEmployee> result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), anotherCompany.getUid());
        assertThat(result).isNotPresent();
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingEmail() throws Exception {
        Optional<CompanyEmployee> result = companyRepository.findEmployeeByEmailForUid("non.existing@email.com", company.getUid());
        assertThat(result).isNotPresent();
    }

    @Test
    public void findEmployeeByEmailForUidNonExistingCompany() throws Exception {
        Optional<CompanyEmployee> result = companyRepository.findEmployeeByEmailForUid(userAccount.getEmail(), UUID.randomUUID());
        assertThat(result).isNotPresent();
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void onlyOneEmployeeCanBeInSameCompany() {
        UserAccount userAccount = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, userAccount, Role.Viewer);
        companyRepository.addEmployee(company, userAccount, Role.Editor);
    }

    @Test
    public void addEmployee() throws Exception {
        UserAccount anotherUserAccount = Factory.userAccount();
        userRepository.save(anotherUserAccount);
        companyRepository.addEmployee(company, anotherUserAccount, Role.Admin);

        Optional<CompanyEmployee> employee = new ArrayList<>(companyRepository.findOne(company.getUid()).getEmployees()).stream()
                .filter(e -> e.getMember().getEmail().equals(anotherUserAccount.getEmail()))
                .findFirst();

        assertThat(employee).isPresent();
        assertThat(employee.get().getRole()).isEqualTo(Role.Admin);
        assertThat(employee.get().getCompany()).isEqualTo(company);
        assertThat(employee.get().getMember()).isEqualTo(anotherUserAccount);
        assertThat(employee.get().getCreatedAt()).isNotNull();
        assertThat(employee.get().getUpdatedAt()).isNotNull();
    }

    @Test
    public void getEmployee() throws Exception {
        UserAccount userAccount = userRepository.save(Factory.userAccount());
        companyRepository.addEmployee(company, userAccount, Role.Owner);

        Optional<CompanyEmployee> departmentEmployee = companyRepository.findEmployeeByUserAccount(userAccount);

        assertThat(departmentEmployee).isPresent();
        assertThat(departmentEmployee.get().getRole()).isEqualTo(Role.Owner);
        assertThat(departmentEmployee.get().getCompany()).isEqualTo(company);
        assertThat(departmentEmployee.get().getMember()).isEqualTo(userAccount);
    }

    @Test
    public void getNonExistingEmployee() throws Exception {
        UserAccount userAccount = userRepository.save(Factory.userAccount());
        Optional<CompanyEmployee> departmentEmployee = companyRepository.findEmployeeByUserAccount(userAccount);

        assertThat(departmentEmployee).isNotPresent();
    }

    @Test
    public void name() throws Exception {
    }

}
