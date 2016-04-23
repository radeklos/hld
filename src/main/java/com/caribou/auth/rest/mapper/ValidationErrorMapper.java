package com.caribou.auth.rest.mapper;

import com.caribou.auth.rest.dto.ErrorField;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ValidationErrorMapper {

    public static Map<String, ErrorField> map(List<FieldError> fieldErrors) {
        Map<String, ErrorField> validationErrors = new HashMap<>();
        for (int i = 0, fieldErrorsSize = fieldErrors.size(); i < fieldErrorsSize; i++) {
            FieldError fieldError = fieldErrors.get(i);
            validationErrors.put(fieldError.getField(), ValidationErrorMapper.map(fieldError));
        }
        return validationErrors;
    }

    public static ErrorField map(FieldError fieldError) {
        ErrorField validationError = new ErrorField();
        validationError.setCode(fieldError.getCode());
        validationError.setDefaultMessage(fieldError.getDefaultMessage());
        validationError.setRejectedValue(fieldError.getRejectedValue());
        return validationError;
    }

}
