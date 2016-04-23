package com.caribou.auth.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;


@Service
public class UserService {

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

}
