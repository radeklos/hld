package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorField {

    @JsonProperty
    String code;

    @JsonProperty
    String defaultMessage;

    @JsonProperty
    Object rejectedValue;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}
