package com.caribou.auth.rest;

import com.caribou.auth.rest.dto.Error;
import com.caribou.auth.rest.mapper.ErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@ControllerAdvice(annotations = RestController.class)
public class ControllerValidationHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public Error processValidationError(MethodArgumentNotValidException ex) {
        Error err = ErrorMapper.map(ex);
        err.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        return err;
    }

}

