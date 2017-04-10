package com.caribou.company.domain;

public enum Role {

    Admin, // Can do everything - super user of whole system - should be only one or authorised person
    Owner, // Owner of the company
    Editor, // Can edit or invite to his department
    Viewer; // Employee who can only do changes on his profile and objects

    public String getName() {  // used in query because name is not accessible
        return name();
    }

}
