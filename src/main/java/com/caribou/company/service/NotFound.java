package com.caribou.company.service;

public class NotFound extends Exception {

    private String departmentName;

    public NotFound(String message, String departmentName) {
        super(message);
        this.departmentName = departmentName;
    }

    public NotFound() {

    }

    public String getDepartmentName() {
        return departmentName;
    }
}
