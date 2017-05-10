package com.caribou.email.templates;

import com.caribou.auth.domain.UserAccount;
import lombok.Builder;
import lombok.Getter;

@Builder
public class Welcome implements EmailTemplate {

    @Getter
    private final UserAccount user;

    @Override
    public HtmlTemplates getHtmlTemplate() {
        return HtmlTemplates.Welcome;
    }

    @Override
    public String getSubject() {
        return "email.welcome.subject";
    }

}
