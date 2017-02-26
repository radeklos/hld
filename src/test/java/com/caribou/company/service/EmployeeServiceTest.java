package com.caribou.company.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.DepartmentEmployee;
import com.caribou.company.domain.Invitation;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import com.caribou.company.repository.InvitationRepository;
import com.caribou.company.service.parser.EmployeeCsvParser;
import com.caribou.email.providers.EmailSender;
import com.sun.tools.javac.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import rx.observers.TestSubscriber;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;


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
    private InvitationRepository invitationRepository;

    @MockBean
    private EmailSender emailSender;

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
        assertThat(employee.getRemainingDaysOff()).isEqualByComparingTo(BigDecimal.valueOf(row.getReamingHoliday()));
    }

    @Test
    public void getDepartmentEmployeeFromDatabase() throws Exception {
        UserAccount userAccount = userRepository.save(Factory.userAccount());
        Department department = Factory.department(company);
        company.setDepartments(Collections.singleton(department));

        EmployeeCsvParser.Row row = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                userAccount.getEmail(),
                department.getName(),
                faker.number().randomDouble(2, 0, 30)
        );

        DepartmentEmployee employee = employeeService.createDepartmentEmployee(row, company);

        UserAccount user = employee.getMember();
        assertThat(user.getFirstName()).isNotEqualTo(row.getFirstName());
        assertThat(user.getLastName()).isNotEqualTo(row.getLastName());
        assertThat(user).isEqualTo(userAccount);

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

        TestSubscriber<DepartmentEmployee> testSubscriber = new TestSubscriber<>();
        employeeService.importEmployee(Arrays.asList(empl1, empl2), company).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        department1 = departmentRepository.findOne(department1.getUid());
        assertThat(department1.getEmployees().size()).isEqualTo(1);

        department2 = departmentRepository.findOne(department2.getUid());
        assertThat(department2.getEmployees().size()).isEqualTo(1);

        company = companyRepository.findOne(department1.getCompany().getUid());
        assertThat(company.getEmployees().size()).isEqualTo(2);
    }

    @Test
    public void failedToSaveEmployeeIntoNonExistingDepartment() throws Exception {
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

        TestSubscriber<DepartmentEmployee> testSubscriber = new TestSubscriber<>();
        employeeService.importEmployee(Arrays.asList(empl1, empl2), company).subscribe(testSubscriber);
        testSubscriber.assertError(RuntimeException.class);

        department = departmentRepository.findOne(department.getUid());
        assertThat(department.getEmployees().toArray()).isNotEmpty();

        company = companyRepository.findOne(department.getCompany().getUid());
        assertThat(company.getEmployees().toArray()).hasSize(1);

        assertThat(userRepository.findByEmail(empl1.getEmail())).isPresent();
        assertThat(userRepository.findByEmail(empl2.getEmail())).isNotPresent();
    }

    @Test
    public void sendInvitationEmail() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        EmployeeCsvParser.Row employee = new EmployeeCsvParser.Row(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                department.getName(),
                faker.number().randomDouble(2, 0, 30)
        );

        TestSubscriber<Pair> testSubscriber = new TestSubscriber<>();
        employeeService.performImport(Collections.singletonList(employee), company).subscribe(testSubscriber);

        verify(emailSender, times(1)).send(any(), any());

        Optional<Invitation> invitation = invitationRepository.findByUserEmail(employee.getEmail());
        assertThat(invitation).isPresent();
    }
}
