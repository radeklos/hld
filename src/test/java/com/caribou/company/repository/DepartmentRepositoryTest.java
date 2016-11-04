package com.caribou.company.repository;

import com.caribou.Factory;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class DepartmentRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    private Company company;

    private UserAccount userAccount;

    private Department department;

    @Before
    public void setUp() throws Exception {
        company = Factory.company();
        companyRepository.save(company);

        userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        company = companyRepository.findOne(company.getUid());

        department = Factory.department(company);
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
    }

    @Test
    public void addEmployeeIntoDepartment() {
        department.addEmployee(userAccount, Role.Admin);
        departmentRepository.save(department);

        Department refreshed = departmentRepository.findOne(department.getUid());
        assertThat(refreshed.getEmployees()).hasSize(1);
    }

    @Test
    public void changeRoleEmployeeIntoDepartment() {
        department.addEmployee(userAccount, Role.Admin);
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
        assertThat(department.getEmployees().iterator().next().getRole()).isEqualTo(Role.Admin);

        department.addEmployee(userAccount, Role.Editor);
        departmentRepository.save(department);

        Department refreshed = departmentRepository.findOne(department.getUid());
        assertThat(refreshed.getEmployees().iterator().next().getRole()).isEqualTo(Role.Editor);
    }

}
