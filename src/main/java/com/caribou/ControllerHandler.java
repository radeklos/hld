package com.caribou;

import com.caribou.auth.rest.dto.Error;
import com.caribou.auth.rest.dto.ErrorField;
import com.caribou.auth.rest.mapper.ErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice(annotations = RestController.class)
public class ControllerHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Error processValidationError(MethodArgumentNotValidException ex) {
        Error err = ErrorMapper.map(ex);
        err.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return err;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Error missingParameter(MissingServletRequestParameterException ex) {
        Map<String, ErrorField> errors = new HashMap<>();
        errors.put(ex.getParameterName(), ErrorField.builder()
                .code("missing parameter")
                .defaultMessage(ex.getMessage())
                .build());

        Error error = new Error(HttpStatus.BAD_REQUEST);
        error.setValidationErrors(errors);
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error entityNotFound(Exception ex) {
        return new Error(HttpStatus.NOT_FOUND);
    }

}
