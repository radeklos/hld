package com.caribou.auth.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorField {

    @JsonProperty
    String code;

    @JsonProperty
    String defaultMessage;

    @JsonProperty
    Object rejectedValue;

    public void setCode(String code) {
        this.code = code;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

}
