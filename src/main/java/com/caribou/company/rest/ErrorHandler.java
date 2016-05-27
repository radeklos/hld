package com.caribou.company.rest;

import com.caribou.company.service.NotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;


public class ErrorHandler {

    public static ResponseEntity h(Throwable e) {
        if (e instanceof NotFound) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (e instanceof AccessDeniedException) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

}
