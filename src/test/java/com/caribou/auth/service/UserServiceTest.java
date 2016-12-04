package com.caribou.auth.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;


public class UserServiceTest extends IntegrationTests {

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
