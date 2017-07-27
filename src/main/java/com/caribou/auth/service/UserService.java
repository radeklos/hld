package com.caribou.auth.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.Welcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder encoder;

    private final UserRepository userRepository;

    private final EmailSender emailSender;

    @Autowired
    public UserService(BCryptPasswordEncoder encoder, UserRepository userRepository, EmailSender emailSender) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public UserAccount create(final UserAccount userAccount) {
        return Observable.<UserAccount>create(subscriber -> {
            try {
                userAccount.setPassword(encoder.encode(userAccount.getPassword()));
                UserAccount user = userRepository.save(userAccount);
                sendInvitationEmail(user);
                subscriber.onNext(userAccount);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        }).toBlocking().first();
    }

    public UserAccount createUser(UserAccount userAccount) {
        userAccount.setPassword(encoder.encode(userAccount.getPassword()));
        UserAccount user = userRepository.save(userAccount);
        sendInvitationEmail(user);
        return userAccount;
    }

    private void sendInvitationEmail(UserAccount userAccount) {
        Email email = Email.builder()
                .to(userAccount)
                .template(Welcome.builder().user(userAccount).build())
                .build();
        emailSender.send(email);
    }

    public Observable<UserAccount> findByEmail(String email) {
        return Observable.create(subscriber -> {
            try {
                Optional<UserAccount> entity = userRepository.findByEmail(email);
                if (entity.isPresent()) {
                    subscriber.onNext(entity.get());
                } else {
                    subscriber.onNext(null);
                }
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> userAccount = userRepository.findByEmail(username);
        return userAccount.
                <UserDetails>map(userAccount1 -> new User(
                        username,
                        userAccount1.getPassword(),
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"))
                )
                .orElse(null);
    }

    public Optional<UserAccount> getByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
