package com.caribou.auth.rest.mapper;

import com.caribou.auth.rest.dto.ErrorField;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ValidationErrorMapper {

    public static Map<String, ErrorField> map(List<FieldError> fieldErrors) {
        Map<String, ErrorField> validationErrors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            validationErrors.put(fieldError.getField(), ValidationErrorMapper.map(fieldError));
        }
        return validationErrors;
    }

    public static ErrorField map(FieldError fieldError) {
        return ErrorField.builder()
                .code(fieldError.getCode())
                .defaultMessage(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }

}
