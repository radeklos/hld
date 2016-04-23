package com.caribou.auth.service;

import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
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
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }

    @Test
    public void testCreate() throws Exception {
        TestSubscriber<UserAccount> testSubscriber = new TestSubscriber<>();

        userService.create(new UserAccount("email@emial.com", "1234")).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        UserAccount us = testSubscriber.getOnNextEvents().get(0);
        Assert.assertNotNull(us.getUid());
    }

}
