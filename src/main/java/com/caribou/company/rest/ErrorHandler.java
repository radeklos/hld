package com.caribou.company.rest;

import com.caribou.auth.rest.dto.Error;
import com.caribou.auth.rest.dto.ErrorField;
import com.caribou.company.service.NotFound;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ErrorHandler {

    public static ResponseEntity h(Throwable e) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (e instanceof NotFound) {
            return new ResponseEntity(headers, HttpStatus.NOT_FOUND);
        } else if (e instanceof AccessDeniedException) {
            return new ResponseEntity(headers, HttpStatus.FORBIDDEN);
        } else if (e instanceof DataIntegrityViolationException) {
            return new ResponseEntity(parseError((DataIntegrityViolationException) e), headers, HttpStatus.CONFLICT);
        }
        return new ResponseEntity(headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static Error parseError(DataIntegrityViolationException e) {
        List<String> matches = parseException(e.getCause().getCause());
        ErrorField errorField = new ErrorField();
        errorField.setCode("must be unique");
        errorField.setDefaultMessage("Email is already taken");
        errorField.setRejectedValue(matches.get(1));

        Error error = new Error(HttpStatus.CONFLICT);
        error.setValidationErrors(new HashMap<String, ErrorField>() {{
            put(matches.get(0), errorField);
        }});
        return error;
    }

    private static List<String> parseException(Throwable ex) {
        Matcher matcher = Pattern.compile("\\(([^)]+)\\)").matcher(ex.getMessage());
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

}
