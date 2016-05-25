package com.caribou.company.service;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.company.domain.Company;
import com.caribou.company.domain.Role;
import com.caribou.company.repository.CompanyRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
public class CompanyServiceTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        companyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void create() throws Exception {
        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        Company company = Company.newBuilder()
                .name("name")
                .defaultDaysOff(10)
                .build();
        companyRepository.save(company);
        UserAccount userAccount = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcab")
                .build();
        userRepository.save(userAccount);
        company.addEmployee(userAccount, Role.Owner);
        companyService.create(company).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();

        Company created = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(created.getUid());
        Assert.assertEquals(1, created.getEmployees().size());
    }

    @Test
    public void update() throws Exception {
        Company company = new Company("name", 10);
        companyRepository.save(company);

        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();

        companyService.update(company.getUid(), new Company("new name", 20)).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        Company updated = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(company.getUid(), updated.getUid());
        Assert.assertEquals("new name", updated.getName());
        Assert.assertEquals(new Integer(20), updated.getDefaultDaysOff());
        Assert.assertNotNull(updated.getCreatedAt());
        Assert.assertNotNull(updated.getUpdatedAt());
    }

    @Test
    public void updateNonExistingObject() throws Exception {
        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.update(0L, new Company("new name", 20)).subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void getNonExistingObject() throws Exception {
        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.get(0L).subscribe(testSubscriber);
        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void get() throws Exception {
        Company company = new Company("name", 10);
        companyRepository.save(company);

        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.get(company.getUid()).subscribe(testSubscriber);

        Company got = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(company.getUid(), got.getUid());
    }

    @Test
    public void getByEmployeeEmail() {
        Company company = Company.newBuilder().name("name").defaultDaysOff(10).build();
        companyRepository.save(company);
        UserAccount user = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(user);
        company.addEmployee(user, Role.Owner);
        companyRepository.save(company);

        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.getForEmployeeEmail(company.getUid(), user.getEmail()).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        Company got = testSubscriber.getOnNextEvents().get(0);
        Assert.assertEquals(company.getUid(), got.getUid());
    }

    @Test
    public void getByEmployeeEmailNonExistingEmail() {
        Company company = Company.newBuilder().name("name").defaultDaysOff(10).build();
        companyRepository.save(company);
        UserAccount user = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(user);
        company.addEmployee(user, Role.Owner);
        companyRepository.save(company);

        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.getForEmployeeEmail(company.getUid(), "non.existing@email.com").subscribe(testSubscriber);

        testSubscriber.assertError(NotFound.class);
    }

    @Test
    public void getByEmployeeEmailNonCompanyUid() {
        Company company = Company.newBuilder().name("name").defaultDaysOff(10).build();
        companyRepository.save(company);
        UserAccount user = UserAccount.newBuilder()
                .email("john.doe@email.com")
                .firstName("John")
                .lastName("Doe")
                .password("abcabc")
                .build();
        userRepository.save(user);
        company.addEmployee(user, Role.Owner);
        companyRepository.save(company);

        TestSubscriber<Company> testSubscriber = new TestSubscriber<>();
        companyService.getForEmployeeEmail(0L, user.getEmail()).subscribe(testSubscriber);

        testSubscriber.assertError(NotFound.class);
    }

}
