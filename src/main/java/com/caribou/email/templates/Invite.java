package com.caribou.email.templates;

import com.caribou.auth.domain.UserAccount;
import lombok.Builder;
import lombok.Getter;

@Builder
public class Invite implements EmailTemplate {

    @Getter
    private final UserAccount user;

    @Getter
    private final String departmentName;

    @Getter
    private final String companyName;

    @Getter
    private final String token;

    @Override
    public HtmlTemplates getHtmlTemplate() {
        return HtmlTemplates.Invite;
    }

}
