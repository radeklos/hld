package com.caribou.company.repository;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class DepartmentRepositoryTest extends IntegrationTests {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private UserAccount userAccount;

    private Department department;

    private Company company;

    @Before
    public void setUp() throws Exception {
        company = Factory.company();
        companyRepository.save(company);

        userAccount = Factory.userAccount();
        userRepository.save(userAccount);

        company.addEmployee(userAccount, Role.Viewer);
        companyRepository.save(company);

        company = companyRepository.findOne(company.getUid());

        department = Factory.department(company, userAccount);
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
    }

    @Test
    public void getEmployee() throws Exception {
        departmentRepository.addEmployee(department, userAccount);
        departmentRepository.save(department);

        Optional<DepartmentEmployee> departmentEmployee = departmentRepository.getEmployee(department, userAccount);

        assertThat(departmentEmployee).isPresent();
        assertThat(departmentEmployee.get().getDepartment()).isEqualTo(department);
        assertThat(departmentEmployee.get().getMember()).isEqualTo(userAccount);
    }

    @Test
    public void getNonExistingEmployee() throws Exception {
        Optional<DepartmentEmployee> departmentEmployee = departmentRepository.getEmployee(department, userAccount);

        assertThat(departmentEmployee).isNotPresent();
    }

}
