package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.util.Map;


public class Error {

    @JsonProperty("errors")
    Map<String, ErrorField> validationErrors;

    @JsonProperty
    String object;

    @JsonProperty
    Status status;

    public void setValidationErrors(Map<String, ErrorField> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setStatus(HttpStatus code) {
        status = new Status(code.value(), code.getReasonPhrase());
    }

    private class Status {

        @JsonProperty
        Integer code;

        @JsonProperty
        String reasonPhrase;

        public Status(Integer code, String reasonPhrase) {
            this.code = code;
            this.reasonPhrase = reasonPhrase;
        }
    }
}
