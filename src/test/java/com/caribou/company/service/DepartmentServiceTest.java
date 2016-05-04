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
        Assert.assertEquals(10, (int) departmentResult.getDaysOf());
        Assert.assertEquals("company", departmentResult.getCompany().getName());
    }

//    @Test
//    public void update() throws Exception {
//        Company company = new Company("name", 10);
//        departmentRepository.save(company);
//
//        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
//
//        departmentService.update(company.getUid(), new Company("new name", 20)).subscribe(testSubscriber);
//        testSubscriber.assertNoErrors();
//
//        Company updated = testSubscriber.getOnNextEvents().get(0);
//        Assert.assertEquals(company.getUid(), updated.getUid());
//        Assert.assertEquals("new name", updated.getName());
//        Assert.assertEquals(new Integer(20), updated.getDefaultDaysOf());
//        Assert.assertNotNull(updated.getCreatedAt());
//        Assert.assertNotNull(updated.getUpdatedAt());
//    }
//
//    @Test
//    public void updateNonExistingObject() throws Exception {
//        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
//        departmentService.update(0l, new Company("new name", 20)).subscribe(testSubscriber);
//        testSubscriber.assertError(NotFound.class);
//    }
//
//    @Test
//    public void getNonExistingObject() throws Exception {
//        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
//        departmentService.get(0l).subscribe(testSubscriber);
//        testSubscriber.assertError(NotFound.class);
//    }
//
//    @Test
//    public void get() throws Exception {
//        Company company = new Company("name", 10);
//        departmentRepository.save(company);
//
//        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
//        departmentService.get(company.getUid()).subscribe(testSubscriber);
//
//        Company got = testSubscriber.getOnNextEvents().get(0);
//        Assert.assertEquals(company.getUid(), got.getUid());
//    }

}
