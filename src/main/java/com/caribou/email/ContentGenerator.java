package com.caribou.email;

import com.caribou.email.templates.EmailTemplate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;

@Component
public class ContentGenerator {

    private final SpringTemplateEngine templateEngine;

    private final MessageSource messageSource;

    @Autowired
    public ContentGenerator(SpringTemplateEngine templateEngine, MessageSource messageSource) {
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    public Content generate(EmailTemplate emailTemplate, Locale locale) {
        return Content.builder()
                .html(html(emailTemplate, locale))
                .plain(plain(emailTemplate, locale))
                .subject(messageSource.getMessage(emailTemplate.getSubject(), null, locale))
                .build();
    }

    private String html(EmailTemplate emailTemplate, Locale locale) {
        Context ctx = new Context(locale, contextBuilder(emailTemplate));
        return templateEngine.process(emailTemplate.getHtmlTemplate().getTemplate(), ctx);
    }

    private String plain(EmailTemplate emailTemplate, Locale locale) {
        return "plain";
    }

    private HashMap<String, Object> contextBuilder(EmailTemplate emailTemplate) {
        HashMap<String, Object> context = new HashMap<>();
        context.put("ctx", emailTemplate);
        return context;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Content {

        private String html;

        private String plain;

        private String subject;

    }
}
