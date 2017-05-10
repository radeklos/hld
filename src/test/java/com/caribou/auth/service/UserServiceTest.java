package com.caribou.auth.service;

import com.caribou.Factory;
import com.caribou.IntegrationTests;
import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class UserServiceTest extends IntegrationTests {

    @Autowired
    private BCryptPasswordEncoder encoder;

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

    @Test
    public void invitationEmailIsSentWhenUserIsCreated() throws Exception {
        TestSubscriber<UserAccount> testSubscriber = new TestSubscriber<>();
        UserAccount userAccount = Factory.userAccount();
        userService.create(userAccount).subscribe(testSubscriber);

        verify(emailSender, times(1)).send(any());
    }

    @Test
    public void userPasswordIsEncoded() throws Exception {
        TestSubscriber<UserAccount> testSubscriber = new TestSubscriber<>();
        UserAccount userAccount = Factory.userAccount();
        String password = userAccount.getPassword();
        userService.create(userAccount).subscribe(testSubscriber);

        UserAccount us = testSubscriber.getOnNextEvents().get(0);
        UserAccount savedUser = userRepository.findOne(us.getUid());

        assertThat(us.getPassword()).isNotEqualTo(password);
        assertThat(encoder.matches(password, savedUser.getPassword())).isTrue();
    }

}
