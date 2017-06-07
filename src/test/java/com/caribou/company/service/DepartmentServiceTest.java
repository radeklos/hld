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
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class DepartmentServiceTest extends IntegrationTests {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    private Company company;

    @Before
    public void setUp() throws Exception {
        company = Factory.company();
        companyRepository.save(company);
    }

    @Test
    public void create() throws Exception {
        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        Department department = Factory.department(company);

        departmentService.create(department).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        Department departmentResult = testSubscriber.getOnNextEvents().get(0);
        assertThat(departmentResult.getUid()).isNotNull();
        assertThat(departmentResult.getName()).isEqualTo(department.getName());
        assertThat(departmentResult.getDaysOff()).isEqualTo(10);
        assertThat(departmentResult.getCompany().getName()).isEqualTo(department.getCompany().getName());
    }

    @Test
    public void update() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();

        String newName = Factory.faker.commerce().department();
        Department update = Department.newBuilder().name(newName).daysOff(20).build();
        departmentService.update(department.getUid(), update).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        Department updated = testSubscriber.getOnNextEvents().get(0);
        assertThat(updated.getUid()).isNotNull();
        assertThat(updated.getName()).isEqualTo(newName);
        assertThat(updated.getDaysOff()).isEqualTo(20);
        assertThat(updated.getCompany().getName()).isEqualTo(department.getCompany().getName());
        assertThat(updated.getCreatedAt()).isNotNull();
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    public void updateNonExistingObject() throws Exception {
        Department department = Factory.department(company);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.update("0", department).subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void getNonExistingObject() throws Exception {
        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.get("0").subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void get() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.get(department.getUid()).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        Department got = testSubscriber.getOnNextEvents().get(0);
        assertThat(got.getUid()).isEqualTo(department.getUid());
    }

    @Test
    public void addEmployeeIntoDepartment() throws Exception {
        Department department = Factory.department(company);
        departmentRepository.save(department);

        UserAccount user = Factory.userAccount();
        userRepository.save(user);

        Observable<DepartmentEmployee> returned = departmentService.addEmployeeRx(new DepartmentEmployee(department, user, BigDecimal.TEN, Role.Viewer));

        TestSubscriber testSubscriber = new TestSubscriber<>();
        returned.subscribe(testSubscriber);

        department = departmentRepository.findOne(department.getUid());
        List<DepartmentEmployee> employees = department.getEmployees().stream().collect(Collectors.toList());

        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getRole()).isEqualTo(Role.Viewer);
        assertThat(employees.get(0).getMember().getEmail()).isEqualTo(user.getEmail());
    }

    @Ignore
    public void overwritePreviousEmployeeDepartment() throws Exception {
        Department department1 = Factory.department(company);
        Department department2 = Factory.department(company);
        departmentRepository.save(Arrays.asList(department1, department2));

        UserAccount user = Factory.userAccount();
        userRepository.save(user);


        TestSubscriber<DepartmentEmployee> sub1 = new TestSubscriber<>();
        Observable<DepartmentEmployee> addEmployee = departmentService.addEmployeeRx(new DepartmentEmployee(department1, user, BigDecimal.TEN, Role.Viewer));
        addEmployee.subscribe(sub1);
        sub1.assertNoErrors();

        TestSubscriber<DepartmentEmployee> sub2 = new TestSubscriber<>();
        addEmployee = departmentService.addEmployeeRx(new DepartmentEmployee(department2, userRepository.findOne(user.getUid()), BigDecimal.TEN, Role.Viewer));
        addEmployee.subscribe(sub2);
        sub2.assertNoErrors();

        department1 = departmentRepository.findOne(department1.getUid());
        AssertionsForClassTypes.assertThat(department1.getEmployees().size()).isEqualTo(0);

        department2 = departmentRepository.findOne(department2.getUid());
        AssertionsForClassTypes.assertThat(department2.getEmployees().size()).isEqualTo(1);

        company = companyRepository.findOne(department1.getCompany().getUid());
        AssertionsForClassTypes.assertThat(company.getEmployees().size()).isEqualTo(1);
    }

}
