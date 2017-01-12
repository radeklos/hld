package com.caribou.company.service;

import com.caribou.Factory;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.EmailSender;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class EmployeeServiceTest {

    private EmployeeService employeeService;

    private DepartmentRepository departmentRepository;

    private UserRepository userRepository;

    private EmailSender emailSender;

    private Faker faker = new Faker();

    @Before
    public void setUp() throws Exception {
        departmentRepository = mock(DepartmentRepository.class);
        userRepository = mock(UserRepository.class);
        emailSender = mock(EmailSender.class);

        employeeService = new EmployeeService(
                departmentRepository,
                userRepository,
                emailSender
        );
    }

    @Test
    public void createDepartmentEmployee() throws Exception {
        Company company = Factory.company();
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

//    @Test
//    public void saveMultipleEmployees() throws Exception {
//        Company company = Factory.company();
//        Department department = Factory.department(company);
//        company.setDepartments(Collections.singleton(department));
//
//        EmployeeCsvParser.Row row = new EmployeeCsvParser.Row(
//                faker.name().firstName(),
//                faker.name().lastName(),
//                faker.internet().emailAddress(),
//                department.getName(),
//                faker.number().randomDouble(2,0, 30)
//        );
//        employeeService.importEmployee(Arrays.asList(row), company);
//    }
//
//    @Test
//    public void failedTransactionDueInvalidDepartmentName() throws Exception {
//        Company company = Factory.company();
//        Department department = Factory.department(company);
//        company.setDepartments(Collections.singleton(department));
//
//        EmployeeCsvParser.Row row = new EmployeeCsvParser.Row(
//                faker.name().firstName(),
//                faker.name().lastName(),
//                faker.internet().emailAddress(),
//                department.getName(),
//                faker.number().randomDouble(2,0, 30)
//        );
//        employeeService.importEmployee(Arrays.asList(row), company);
//    }


}
