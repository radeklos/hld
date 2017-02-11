package com.caribou.email.templates;

public abstract class Welcome implements EmailTemplate {

    @Override
    public HtmlTemplates getHtmlTemplate() {
        return HtmlTemplates.Welcome;
    }
}
