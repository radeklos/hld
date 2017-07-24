package com.caribou.email.templates;

import com.caribou.holiday.domain.Leave;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
public class LeaveApproved implements EmailTemplate {

    @Getter
    private final Leave leave;

    @Override
    public HtmlTemplates getHtmlTemplate() {
        return HtmlTemplates.LeaveApproved;
    }

    @Override
    public String getSubject() {
        return "email.leave.approved";
    }

}
