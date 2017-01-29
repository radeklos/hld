package com.caribou.company.domain;

public enum Role {

    Admin, // Can do everything - super user of whole system
    Owner, // Owner of the company
    Editor, // Can edit or invite to his department
    Viewer; // Employee who can only do changes on his

    public String getName() {  // used in query because name is not accessible
        return name();
    }
}
