package com.caribou.company.service;

import com.caribou.WebApplication;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Department;
import com.caribou.company.repository.DepartmentRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.observers.TestSubscriber;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class DepartmentServiceTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentService departmentService;

    @After
    public void tearDown() throws Exception {
        departmentRepository.deleteAll();
    }

    @Test
    public void create() throws Exception {
        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();

        Department department = Department.newBuilder()
                .company(Company.newBuilder().name("company").defaultDaysOf(10).build())
                .name("department")
                .daysOf(10)
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
                .company(Company.newBuilder().name("company").defaultDaysOf(10).build())
                .name("department")
                .daysOf(10)
                .build();
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();

        Department update = Department.newBuilder().name("new name").daysOf(20).build();
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
                .daysOf(10)
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
                .company(Company.newBuilder().name("company").defaultDaysOf(10).build())
                .name("department")
                .daysOf(10)
                .build();
        departmentRepository.save(department);

        TestSubscriber<Department> testSubscriber = new TestSubscriber<>();
        departmentService.get(department.getUid()).subscribe(testSubscriber);

        Department got = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(department.getUid(), got.getUid());
    }

}
