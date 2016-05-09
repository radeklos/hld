package com.caribou.auth.rest;


import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.rest.dto.UserAccountDto;
import com.caribou.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public Observable<UserAccountDto> get(@PathParam("uid") String uid) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Observable<UserAccountDto> bbb = userService.findByEmail(userDetails.getUsername()).map(new Func1<UserAccount, UserAccountDto>() {
            @Override
            public UserAccountDto call(UserAccount userAccount) {
                return modelMapper.map(userAccount, UserAccountDto.class);
            }
        });

        return bbb;
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json")
    public UserAccountDto create(@Valid @RequestBody UserAccountDto newUser, HttpServletResponse response) {
        UserAccount user = convertToEntity(newUser);

        userService.create(user).subscribe(new Subscriber<UserAccount>() {
            @Override
            public void onCompleted() {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof DataIntegrityViolationException) {
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
