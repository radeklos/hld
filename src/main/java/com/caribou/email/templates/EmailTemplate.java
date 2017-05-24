package com.caribou.email.templates;

public interface EmailTemplate {

    HtmlTemplates getHtmlTemplate();

    String getSubject();

}
