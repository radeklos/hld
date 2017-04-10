package com.caribou.email.templates;

public enum HtmlTemplates {

    Invite("employee_invitation"),
    Welcome("welcome");

    private String template;

    HtmlTemplates(String s) {
        template = s;
    }

    public String getTemplate() {
        return template;
    }
}
