package com.caribou.auth.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public Observable<UserAccount> create(final UserAccount userAccount) {
        return Observable.create(subscriber -> {
            try {
                userRepository.save(userAccount);
                subscriber.onNext(userAccount);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
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
        if (userAccount.isPresent()) {
            return new User(username, userAccount.get().getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        }
        return null;
    }

    public Optional<UserAccount> getByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
