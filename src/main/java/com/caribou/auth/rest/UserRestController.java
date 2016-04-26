package com.caribou.auth.rest;


import com.caribou.auth.rest.dto.UserAccount;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping("/v1/users")
public class UserRestController {

    @RequestMapping(method = RequestMethod.PUT)
    public UserAccount create(@Valid @RequestBody UserAccount newUser, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CREATED);
        return newUser;
    }

}
