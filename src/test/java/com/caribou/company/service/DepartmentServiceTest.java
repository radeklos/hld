package com.caribou.company.service;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import com.caribou.company.repository.DepartmentRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.observers.TestSubscriber;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DepartmentServiceTest {

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
        company = Company.newBuilder().name("company").defaultDaysOff(10).build();
        companyRepository.save(company);
    }

    @Test
    public void create() throws Exception {
        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();

        Department department = Department.newBuilder()
                .company(company)
                .name("department")
                .daysOff(10)
                .build();

        departmentService.create(department).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        Department departmentResult = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(departmentResult.getUid());
        Assert.assertEquals("department", departmentResult.getName());
        Assert.assertEquals(10, (int) departmentResult.getDaysOff());
        Assert.assertEquals("company", departmentResult.getCompany().getName());
    }

    @Test
    public void update() throws Exception {
        Department department = Department.newBuilder()
                .company(company)
                .name("department")
                .daysOff(10)
                .build();
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();

        Department update = Department.newBuilder().name("new name").daysOff(20).build();
        departmentService.update(department.getUid(), update).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        Department updated = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(updated.getUid());
        Assert.assertEquals("new name", updated.getName());
        Assert.assertEquals(20, (int) updated.getDaysOff());
        Assert.assertEquals("company", updated.getCompany().getName());
        Assert.assertNotNull(updated.getCreatedAt());
        Assert.assertNotNull(updated.getUpdatedAt());
    }

    @Test
    public void updateNonExistingObject() throws Exception {
        Department department = Department.newBuilder()
                .name("department")
                .daysOff(10)
                .build();

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.update(0L, department).subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void getNonExistingObject() throws Exception {
        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.get(0L).subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void get() throws Exception {
        Department department = Department.newBuilder()
                .company(company)
                .name("department")
                .daysOff(10)
                .build();
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.get(department.getUid()).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        Department got = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(department.getUid(), got.getUid());
    }

    @Test
    public void addEmployee() {
        Department department = Department.newBuilder()
                .company(company)
                .name("department")
                .daysOff(10)
                .build();
        departmentRepository.save(department);

        UserAccount user = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(user);

        department.addEmployee(user, Role.Admin);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.create(department).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        Department departmentResult = testSubscriber.getOnNextEvents().get(0);

        Assert.assertEquals(1, departmentResult.getEmployees().size());
    }

}
