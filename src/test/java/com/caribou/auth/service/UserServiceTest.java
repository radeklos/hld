package com.caribou.auth.service;

import com.caribou.Factory;
import com.caribou.WebApplication;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebApplication.class)
@WebAppConfiguration
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testCreate() throws Exception {
        TestSubscriber<UserAccount> testSubscriber = new TestSubscriber<>();

        userService.create(Factory.userAccount()).subscribe(testSubscriber);
        testSubscriber.assertNoErrors();

        UserAccount us = testSubscriber.getOnNextEvents().get(0);
        assertThat(us.getUid()).isNotNull();
    }

}
