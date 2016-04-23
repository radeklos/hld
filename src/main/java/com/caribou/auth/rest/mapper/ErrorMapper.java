package com.caribou.auth.rest.mapper;

import com.caribou.auth.rest.dto.Error;
import org.springframework.web.bind.MethodArgumentNotValidException;


public class ErrorMapper {

    public static Error map(MethodArgumentNotValidException ex) {
        Error err = new Error();
        err.setObject(ex.getBindingResult().getObjectName());
        err.setValidationErrors(ValidationErrorMapper.map(ex.getBindingResult().getFieldErrors()));

        return err;
    }

}
