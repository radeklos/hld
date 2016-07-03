package com.caribou.company.repository;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        company = Company.newBuilder().name("company").defaultDaysOff(10).build();
        companyRepository.save(company);

        userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(userAccount);

        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        company = companyRepository.findOne(company.getUid());

        department = Department.newBuilder().name("department").company(company).daysOff(10).build();
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
    }

    @Test
    public void addEmployeeIntoDepartment() {
        department.addEmployee(userAccount, Role.Admin);
        departmentRepository.save(department);

        Department refreshed = departmentRepository.findOne(department.getUid());
        assertEquals(1, refreshed.getEmployees().size());
    }

    @Test
    public void changeRoleEmployeeIntoDepartment() {
        department.addEmployee(userAccount, Role.Admin);
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
        assertEquals(Role.Admin, department.getEmployees().iterator().next().getRole());

        department.addEmployee(userAccount, Role.Editor);
        departmentRepository.save(department);

        Department refreshed = departmentRepository.findOne(department.getUid());
        assertEquals(Role.Editor, refreshed.getEmployees().iterator().next().getRole());
    }

}