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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class DepartmentRepositoryTest extends IntegrationTests {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

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

        department = Factory.department(company);
        departmentRepository.save(department);

        department = departmentRepository.findOne(department.getUid());
    }

    @Test
    public void addEmployee() throws Exception {
        departmentRepository.addEmployee(department, userAccount, Role.Admin);

        List<DepartmentEmployee> employees = departmentRepository.findOne(department.getUid()).getEmployees().stream().collect(Collectors.toList());

        assertThat(employees).isNotEmpty();
        assertThat(employees.get(0).getRole()).isEqualTo(Role.Admin);
        assertThat(employees.get(0).getDepartment()).isEqualTo(department);
        assertThat(employees.get(0).getMember()).isEqualTo(userAccount);
        assertThat(employees.get(0).getCreatedAt()).isNotNull();
        assertThat(employees.get(0).getUpdatedAt()).isNotNull();
    }

    @Test
    public void updateEmployeeRole() throws Exception {
        departmentRepository.addEmployee(department, userAccount, Role.Admin);
        departmentRepository.save(department);

        departmentRepository.updateEmployee(userAccount, Role.Viewer);

        List<DepartmentEmployee> employees = departmentRepository.findOne(department.getUid()).getEmployees().stream().collect(Collectors.toList());
        assertThat(employees.get(0).getRole()).isEqualTo(Role.Viewer);
        assertThat(employees.get(0).getDepartment()).isEqualTo(department);
        assertThat(employees.get(0).getMember()).isEqualTo(userAccount);
    }

    @Test
    public void getEmployee() throws Exception {
        departmentRepository.addEmployee(department, userAccount, Role.Owner);
        departmentRepository.save(department);

        Optional<DepartmentEmployee> departmentEmployee = departmentRepository.getEmployee(department, userAccount);

        assertThat(departmentEmployee).isPresent();
        assertThat(departmentEmployee.get().getRole()).isEqualTo(Role.Owner);
        assertThat(departmentEmployee.get().getDepartment()).isEqualTo(department);
        assertThat(departmentEmployee.get().getMember()).isEqualTo(userAccount);
    }

    @Test
    public void getNonExistingEmployee() throws Exception {
        Optional<DepartmentEmployee> departmentEmployee = departmentRepository.getEmployee(department, userAccount);

        assertThat(departmentEmployee).isNotPresent();
    }

}
