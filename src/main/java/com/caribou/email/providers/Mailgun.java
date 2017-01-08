package com.caribou.email.providers;

import com.caribou.email.Company;
import com.caribou.email.Email;
import com.caribou.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


@Component
public class Mailgun implements EmailSender {

    private static final String ENCODING = "UTF-8";
    private final JavaMailSender sender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public Mailgun(JavaMailSender sender, SpringTemplateEngine templateEngine) {
        this.sender = sender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void send(Email email, Locale locale) throws UnsupportedEncodingException, MessagingException {
        Company company = new Company(
                "hld",
                null,
                null,
                null
        );

        Context ctx = new Context(locale);
        ctx.setVariable("company", company);
        final String htmlContent = templateEngine.process("template", ctx);

        final MimeMessage mimeMessage = this.sender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING);
        message.setFrom(new InternetAddress(email.getFrom().getAddress(), email.getFrom().getAlias()));
        message.setTo(new InternetAddress(email.getTo().getAddress(), email.getTo().getAlias()));
        message.setSubject(email.getSubject());
        message.setText("plain", htmlContent);

        sender.send(message.getMimeMessage());
    }

}
