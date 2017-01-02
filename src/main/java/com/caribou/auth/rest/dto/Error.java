package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.http.HttpStatus;

import java.util.Map;


public class Error {

    @JsonProperty("errors")
    Map<String, ErrorField> validationErrors;

    @JsonProperty
    String object;

    @JsonProperty
    Status status;

    public Error() {
    }

    public Error(HttpStatus httpStatus) {
        setStatus(httpStatus);
    }

    public void setStatus(HttpStatus httpStatus) {
        this.status = new Status(httpStatus.value(), httpStatus.getReasonPhrase());
    }

    public void setObject(String object) {
        this.object = object;
    }

    @JsonSetter("status")
    public Error setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Map<String, ErrorField> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, ErrorField> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public class Status {

        @JsonProperty
        Integer code;

        @JsonProperty
        String reasonPhrase;

        public Status() {
        }

        public Status(Integer code, String reasonPhrase) {
            this.code = code;
            this.reasonPhrase = reasonPhrase;
        }
    }

}
