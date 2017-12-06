package com.caribou.email.templates;

public enum HtmlTemplates {

    Invite("employee_invitation"),
    Welcome("welcome"),
    LeaveApproved("leave_approved"),
    LeaveRequest("leave_request");

    private String template;

    HtmlTemplates(String s) {
        template = s;
    }

    public String getTemplate() {
        return template;
    }
}
