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
        UserAccount userAccount = userService.create(Factory.userAccount());
        assertThat(userAccount.getUid()).isNotNull();
    }

    @Test
    public void invitationEmailIsSentWhenUserIsCreated() throws Exception {
        TestSubscriber<UserAccount> testSubscriber = new TestSubscriber<>();
        UserAccount userAccount = Factory.userAccount();
        userService.create(userAccount);

        verify(emailSender, times(1)).send(any());
    }

    @Test
    public void userPasswordIsEncoded() throws Exception {
        UserAccount userAccount = Factory.userAccount();
        String password = userAccount.getPassword();
        UserAccount saved = userService.create(userAccount);

        assertThat(saved.getPassword()).isNotEqualTo(password);
        assertThat(encoder.matches(password, saved.getPassword())).isTrue();
    }

}
