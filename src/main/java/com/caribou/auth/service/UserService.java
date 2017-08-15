package com.caribou.auth.service;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.repository.UserRepository;
import com.caribou.email.Email;
import com.caribou.email.providers.EmailSender;
import com.caribou.email.templates.Welcome;
import ma.glasnost.orika.MapperFacade;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rx.Observable;

import java.beans.FeatureDescriptor;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


@Service
public class UserService implements UserDetailsService {

    private final BCryptPasswordEncoder encoder;

    private final UserRepository userRepository;

    private final EmailSender emailSender;

    @Autowired
    public UserService(BCryptPasswordEncoder encoder, UserRepository userRepository, EmailSender emailSender, ModelMapper modelMapper, MapperFacade mapperFacade) {
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    public UserAccount register(UserAccount userAccount) {
        UserAccount user = create(userAccount);
        sendInvitationEmail(user);
        return user;
    }

    public UserAccount create(UserAccount userAccount) {
        userAccount.setPassword(encoder.encode(userAccount.getPassword()));
        return userRepository.save(userAccount);
    }

    public UserAccount update(UUID uid, UserAccount userAccount) {
        UserAccount original = userRepository.findOne(uid);
        if (userAccount.getPassword() != null) {
            userAccount.setPassword(encoder.encode(userAccount.getPassword()));
        }
        BeanUtils.copyProperties(userAccount, original, getNullPropertyNames(userAccount));
        return userRepository.save(original);
    }

    /**
     * User for naive object mapping where we don't want to set fields to null
     *
     * @param source source object
     * @return list of null property names
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    private void sendInvitationEmail(UserAccount userAccount) {
        Email email = Email.builder()
                .to(userAccount)
                .template(Welcome.builder().user(userAccount).build())
                .build();
        emailSender.send(email, userAccount.getLocale());
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
