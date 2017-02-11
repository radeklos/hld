package com.caribou.email;

import com.caribou.email.templates.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;

@Component
public class ContentGenerator {

    private final SpringTemplateEngine templateEngine;

    @Autowired
    public ContentGenerator(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String html(EmailTemplate emailTemplate, Locale locale) {
        Context ctx = new Context(locale, contextBuilder(emailTemplate));
        return templateEngine.process(emailTemplate.getHtmlTemplate().getTemplate(), ctx);
    }

    public HashMap<String, Object> contextBuilder(EmailTemplate emailTemplate) {
        HashMap<String, Object> context = new HashMap<>();
        context.put("company", new Company("Pietary", "https://pietary.com"));
        context.put("ctx", emailTemplate);
        return context;
    }

}
