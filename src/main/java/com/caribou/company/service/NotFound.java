package com.caribou.company.service;

public class NotFound extends RuntimeException {

    public NotFound(String message) {
        super(message);
    }

    public NotFound() {

    }

}
