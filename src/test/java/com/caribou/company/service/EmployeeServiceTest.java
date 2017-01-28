package com.caribou.company.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.EmailSender;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EmployeeServiceTest extends IntegrationTests {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSender emailSender;

    private Faker faker = new Faker();

    private Company company;

    @Before
    public void setUp() throws Exception {
        company = companyRepository.save(Factory.company());
    }

    @Test
    public void createDepartmentEmployee() throws Exception {
        Department department = Factory.department(company);
        company.setDepartments(Collections.singleton(department));

        EmployeeCsvParser.Row row = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                department.getName(),
                faker.number().randomDouble(2, 0, 30)
        );

        DepartmentEmployee employee = employeeService.createDepartmentEmployee(row, company);

        UserAccount user = employee.getMember();
        assertThat(user.getFirstName()).isEqualTo(row.getFirstName());
        assertThat(user.getLastName()).isEqualTo(row.getLastName());

        assertThat(employee.getDepartment()).isEqualTo(department);
        assertThat(employee.getRole()).isEqualTo(Role.Viewer);
    }

    @Test(expected = NotFound.class)
    public void cannotFindDepartmentForEmployee() throws Exception {
        Company company = Factory.company();
        Department department = Factory.department(company);
        company.setDepartments(Collections.singleton(department));

        EmployeeCsvParser.Row row = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                "some department",
                faker.number().randomDouble(2, 0, 30)
        );
        employeeService.createDepartmentEmployee(row, company);
    }

    @Test
    public void saveMultipleEmployees() throws Exception {
        Department department1 = Factory.department(company);
        Department department2 = Factory.department(company);
        departmentRepository.save(Arrays.asList(department1, department2));

        EmployeeCsvParser.Row empl1 = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                department1.getName(),
                faker.number().randomDouble(2, 0, 30)
        );
        EmployeeCsvParser.Row empl2 = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                department2.getName(),
                faker.number().randomDouble(2, 0, 30)
        );

        employeeService.importEmployee(Arrays.asList(empl1, empl2), company);

        department1 = departmentRepository.findOne(department1.getUid());
        assertThat(department1.getEmployees().size()).isEqualTo(1);

        department2 = departmentRepository.findOne(department2.getUid());
        assertThat(department2.getEmployees().size()).isEqualTo(1);

        company = companyRepository.findOne(department1.getCompany().getUid());
        assertThat(company.getEmployees().size()).isEqualTo(2);
    }

    @Test
    public void failedTransactionDueInvalidDepartmentName() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        EmployeeCsvParser.Row empl1 = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                department.getName(),
                faker.number().randomDouble(2, 0, 30)
        );
        EmployeeCsvParser.Row empl2 = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                "not existing department",
                faker.number().randomDouble(2, 0, 30)
        );
        try {
            employeeService.importEmployee(Arrays.asList(empl1, empl2), company);
        } catch (Exception ex) {

        }

        department = departmentRepository.findOne(department.getUid());
        assertThat(department.getEmployees().toArray()).isEmpty();

        company = companyRepository.findOne(department.getCompany().getUid());
        assertThat(company.getEmployees().toArray()).isEmpty();

        assertThat(userRepository.findByEmail(empl1.getEmail())).isNotPresent();
        assertThat(userRepository.findByEmail(empl2.getEmail())).isNotPresent();
    }

}
