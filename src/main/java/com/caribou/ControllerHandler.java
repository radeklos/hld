package com.caribou;

import com.caribou.auth.rest.dto.Error;
import com.caribou.auth.rest.mapper.ErrorMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Error entityNotFound(Exception ex) {
        Error err = new Error();
        err.setStatus(HttpStatus.NOT_FOUND);
        return err;
    }

}

