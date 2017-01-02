package com.caribou.auth.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.caribou.auth.SecurityConfig.FORM_BASED_LOGIN_ENTRY_POINT;


/**
 * Dummy rest controller which expose unsecured Http OPTIONS on login entrypoint
 */
@RestController
public class AuthController {

    @RequestMapping(value = FORM_BASED_LOGIN_ENTRY_POINT, method = RequestMethod.OPTIONS)
    public String dummy() {
        return null;
    }

}
