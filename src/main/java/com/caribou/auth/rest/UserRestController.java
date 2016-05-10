package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Subscriber;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public UserAccountDto get(@PathVariable("uid") Long uid) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Observable<UserAccountDto> bla = userService.findByEmail(userDetails.getUsername())
                .map(u -> modelMapper.map(u, UserAccountDto.class));
        return bla.toBlocking().first();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserAccountDto create(@Valid @RequestBody UserAccountDto newUser, HttpServletResponse response) {
        UserAccount user = convertToEntity(newUser);

        userService.create(user).subscribe(new Subscriber<UserAccount>() {
            @Override
            public void onCompleted() {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof DataIntegrityViolationException) {  // TODO can also be nulls
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

            @Override
            public void onNext(UserAccount userAccount) {

            }
        });

        return newUser;
    }

    private UserAccount convertToEntity(UserAccountDto newUser) {
        return modelMapper.map(newUser, UserAccount.class);
    }

}
